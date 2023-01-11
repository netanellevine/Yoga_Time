package dataAccessLayer

import Shared.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.N)
class Data {
    private val db = Firebase.firestore
    private val tag = "Database"

    suspend fun addInstructor(userInfo: HashMap<String, Any>) {
        val userId = userInfo["userId"] as String
        if (!checkIfInstructorExists(userId)) {
            val res = postRequest("instructor/create",userInfo)
            if(res == "true"){
                Log.d(tag,"ADDED instructor")
            }
            else{
                Log.d(tag,"Failed to add instructor")
            }

        }
        else{
            Log.w(tag,  "Instructor already exists")
        }
    }

    suspend fun addParticipant(userInfo: HashMap<String, Any>){
        val userId = userInfo["userId"] as String
        if (!checkIfParticipantExists(userId)) {
            //Add the Participant to the Database
            val res = postRequest("participant/create",userInfo)
            if(res == "true"){
                Log.d(tag,"ADDED Participant")
            }
            else{
                Log.d(tag,"Failed to add Participant")
            }
        }
        else{
            Log.w(tag,  "Participant already exists")
        }

    }

     fun checkIfInstructorExists(userId: String): Boolean {
         val res = getRequest("instructor/exists", hashMapOf("userId" to userId))
         if (res == "true"){
             Log.d(tag,"Instructor $userId exists, res:$res")
         }
         else{
             Log.d(tag,"Instructor $userId does not exists, res:$res")
         }
         return res == "true"
    }

    fun checkIfParticipantExists(userId: String): Boolean {


        val res = getRequest("participant/exists", hashMapOf("userId" to userId))
        if (res == "true"){
            Log.d(tag,"Participant $userId exists, res:$res")
        }
        else{
            Log.d(tag,"Participant $userId does not exists, res:$res")
        }
        return res == "true"

    }



    fun addUserToLesson(userId: String,key:String,lesson:Lesson,userToAdd:String){
//        if(userToAdd !in lesson.ParticipantsList && lesson.ParticipantsList.size < lesson.numberOfParticipants){
//            lesson.ParticipantsList.add(userToAdd)
        val lessonInfo: HashMap<String,Any> = hashMapOf(
            "key" to key,
            "userId" to userId,
            "userToAdd" to userToAdd,
            "lesson" to Gson().toJson(lesson)
        )

            val res = postRequest("lesson/addUser",lessonInfo)
            if(res == "true"){
                Log.d(tag,"ADDED user to lesson")
            }
            else{
                Log.d(tag,"Failed to add user to lesson")
            }

    }

    fun removeUserFromLesson(userId: String,key:String,lesson:Lesson,userToAdd:String){
        val lessonInfo: HashMap<String,Any> = hashMapOf(
            "key" to key,
            "userId" to userId,
            "userToAdd" to userToAdd,
            "lesson" to Gson().toJson(lesson)
        )

        val res = postRequest("lesson/removeUser",lessonInfo)
        if(res == "true"){
            Log.d(tag,"Removed user to lesson")
        }
        else{
            Log.d(tag,"Failed to add user to lesson")
        }


    }

    fun addAndRemove(flag:Boolean, userId: String, key:String, lesson:Lesson, userToAdd:String){
            if (flag) {
                addUserToLesson(userId, key, lesson, userToAdd)
            } else {
                removeUserFromLesson(userId, key, lesson, userToAdd)
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
                        inLesson : Boolean,addLesson: (flag:Boolean)-> Unit,year:String) -> Unit) {
        val scope = CoroutineScope(newSingleThreadContext("Add instructor"))
        var res: String = ""
        var wait = true
        scope.launch {
            res = getRequest("lesson/availability", hashMapOf(
                "userId" to userId,
                "date" to date
            ))
            wait = false
        }
        while (wait){}

        val lessons = Gson().fromJson(res,Array<fullLesson>::class.java)
        var startIdentity = 300000
        var layoutId = 400000
        for(fulllesson in lessons){
                val lessonDate = fulllesson.date

                if (lessonDate.contains(date)){
                    val lesson = fulllesson.lesson
                    if (lesson.ParticipantsList.size < lesson.numberOfParticipants) {
                        val inList = userId in lesson.ParticipantsList
                        callback(
                            lessonDate.split("_")[1],
                            startIdentity,
                            layoutId,
                            "${lesson.ParticipantsList.size}/${lesson.numberOfParticipants}",
                            lesson.lessonName,
                            lesson.level,
                            lesson.price.toString(),
                            inList,
                            { flag:Boolean ->
                                addAndRemove(flag,fulllesson.docId, lessonDate, lesson, userId)
                            },
                            lessonDate.split("_")[0]
                        )


                        startIdentity+=100
                        layoutId+=100
                    }
                }

            }
        }



    @OptIn(DelicateCoroutinesApi::class)
    fun validateLesson(
        userId: String,
        lessonInfo: HashMap<String, Any>,
        callback: (message: String) -> Unit
    ): Boolean {
        lessonInfo["userId"] = userId
        val scope = CoroutineScope(newSingleThreadContext("Add instructor"))

        var pop = true
        var wait = true
        scope.launch {
            pop = postRequestValidate("lesson/validate", lessonInfo) == "true"
            wait = false
        }
        while (wait){}
        if (pop){
            callback("added successfully")
        }

        else{
            callback("Couldn't add successfully")
        }

        return pop

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