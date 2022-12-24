package dataAccessLayer

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import Shared.Lesson
import kotlinx.coroutines.tasks.await


class Data {
    private val db = Firebase.firestore
    private val tag = "Database"

    suspend fun addInstructor(userInfo: HashMap<String, Any>) {
        val userId = userInfo["userId"] as String
        if (!checkIfInstructorExists(userId)) {
            //Add the instructor to the Database
            db.collection("Instructors").document(userId).set(userInfo)
                .addOnSuccessListener {
                    Log.d(tag, "Added instructor with ID: $userId ")
                }
                .addOnFailureListener { e ->
                    Log.w(tag, "Error adding document", e)
                }

        }
        else{
            Log.w(tag,  "Instructor already exists")
        }
    }

    suspend fun addParticipant(userInfo: HashMap<String, String>){
        val userId = userInfo["userId"] as String
        if (!checkIfParticipantExists(userId)) {
            //Add the Participant to the Database
            db.collection("Participants").document(userId)
                .set(userInfo)
                .addOnSuccessListener {
                    Log.d(tag, "Added participant with ID:  $userId")
                }
                .addOnFailureListener { e ->
                    Log.w(tag, "Error adding document", e)
                }
        }
        else{
            Log.w(tag,  "Participant already exists")
        }

    }

     suspend fun checkIfInstructorExists(userId: String): Boolean {
        var exists = false
        db.collection("Instructors").document(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if(document != null) {
                    if (document.exists()) {
                        exists = true
                        Log.d("TAG", "Document already exists.")
                    } else {
                        Log.d("TAG", "Document doesn't exist.")
                    }
                }
            } else {
                Log.d("TAG", "Error: ", task.exception)
            }
        }.await()

        return exists
    }

    suspend fun checkIfParticipantExists(userId: String): Boolean {
        var exists = false
        db.collection("Participants").document(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if(document != null) {
                    if (document.exists()) {
                        exists = true
                        Log.d("TAG", "Document already exists.")
                    } else {
                        Log.d("TAG", "Document doesn't exist.")
                    }
                }
            } else {
                Log.d("TAG", "Error: ", task.exception)
            }
        }.await()

        return exists
    }



    fun addUserToLesson(userId: String,key:String,lesson:Lesson,userToAdd:String){
        lesson.ParticipantsList.add(userToAdd)
        val lessonInfo: HashMap<String,Any> = hashMapOf(
            key to Gson().toJson(lesson)
        )
        db.collection("Lessons").document(userId).set(lessonInfo,SetOptions.merge()).addOnCompleteListener {task ->
            if(task.isSuccessful){
                Log.d(tag,"Added to lesson successfuly")
            }
            else{
                Log.w(tag,"Failed to add to lesson")
            }
        }
    }

    private fun addLesson(userId: String, lessonInfo: HashMap<String,Any>){
        db.collection("Lessons").document(userId).set(lessonInfo,SetOptions.merge()).addOnCompleteListener { //, SetOptions.merge()
                task ->
            if (task.isSuccessful){
                Log.d(tag,"Added to database")
            }
            else{
                Log.d(tag,"Couldn't modify hours")
            }
        }
    }

    fun getAvailability(userId: String,date:String,
                        callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String,
                        inLesson : Boolean,addLesson: ()-> Unit) -> Unit) {
        db.collection("Lessons").get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                var startIdentity = 300000
                var layoutId = 400000
                task.result.documents.forEach { doc ->
                    doc.data?.forEach { field ->
                        if (field.key.contains(date)){
                            val lesson = Gson().fromJson(field.value.toString(),Lesson::class.java)
                            if (lesson.ParticipantsList.size < lesson.numberOfParticipants) {
                                val inList = userId in lesson.ParticipantsList
                                callback(
                                    field.key.split("_")[1],
                                    startIdentity,
                                    layoutId,
                                    "${lesson.ParticipantsList.size}/${lesson.numberOfParticipants}",
                                    lesson.lessonName,
                                    lesson.level,
                                    lesson.price.toString(),
                                    inList
                                ) {
                                    addUserToLesson(doc.id,field.key, lesson, userId)
                                }
                            }
                        }
                    }
                }
            }
            else{
                Log.w(tag,"Couldn't search through database please verify internet connection")
            }

        }
    }

    fun validateLesson(
        userId: String,
        lessonInfo: HashMap<String, Any>,
        callback: (message: String) -> Unit
    ){
        db.collection("Lessons").document(userId).get().addOnCompleteListener { Task->
            if (Task.isSuccessful) {
                var addLessonBool = true
                val keyToCompare = lessonInfo.keys.elementAt(0)
                Task.result.data?.forEach { Entry ->
                    if (compareKeys(Entry.key,keyToCompare)){
                        addLessonBool = false
                    }
                }
                if(addLessonBool){
                    Log.d(tag,"Adding to database $lessonInfo")
                    callback("Scheduled successfully")
                    addLesson(userId,lessonInfo)
                }
                else{
                    callback("Busy at that date")
                    Log.d(tag,"Couldn't add to database")
                }
            }
            else{
                Log.d(tag,"Lesson verification failed")
            }
        }
    }

    private fun compareKeys(key:String, keyCompare:String): Boolean {
        val attributesKey = key.split("_")
        val attributeKeyCompare = keyCompare.split("_")
        if(attributesKey[0] == attributeKeyCompare[0]){
            val startEndtime = attributesKey[1].split("-")
            val startEndtimeCompare = attributeKeyCompare[1].split("-")
            if(compareTime(startEndtime[0],startEndtime[1],startEndtimeCompare[0],startEndtimeCompare[1])){
                return true
            }
        }
        return false
    }

    private fun compareTime(timeStart:String, timeEnd:String, timeStartCompare:String, timeEndCompare:String): Boolean {
        var startTime = timeStart.replace(":","").toInt()
        var endTime = timeEnd.replace(":","").toInt()
        var compareStartTime = timeStartCompare.replace(":","").toInt()
        var compareEndTime = timeEndCompare.replace(":","").toInt()

        return inTheMiddle(startTime,compareStartTime,endTime)
                || inTheMiddle(startTime,compareEndTime,endTime)
                || inTheMiddle(compareStartTime,startTime,compareEndTime)
                || inTheMiddle(compareStartTime,endTime,compareEndTime)
    }

    private fun inTheMiddle(x:Int, y:Int, z:Int): Boolean {
        return (x < y) && (y < z)
    }



        fun getInstructorTimeFromDatabase(userId: String,date:String,
    callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String) -> Unit){
        db.collection("Lessons").document(userId).get().addOnCompleteListener { Task->
            if (Task.isSuccessful) {
                    val sortedKeys = Task.result.data?.keys?.sorted()
                    var startIdentity = 300000
                    var layoutId = 400000
                    if (sortedKeys!=null){
                        for (key in sortedKeys){
                            val splitDate = key.split("_")
                            if (date == splitDate[0]){
                                val lesson = Gson().fromJson(Task.result.data!![key].toString(), Lesson::class.java)
                                callback(splitDate[1],startIdentity,layoutId,
                                "${lesson.ParticipantsList.size}/${lesson.numberOfParticipants}",
                                lesson.lessonName,
                                lesson.level,
                                "${lesson.price*lesson.ParticipantsList.size}$")
                                startIdentity += 100
                                layoutId += 100
                            }
                        }
                    }
                }

            else{
                Log.d(tag,"Lesson verification failed")
            }
        }
    }


}