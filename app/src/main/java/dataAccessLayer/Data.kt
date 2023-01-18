package dataAccessLayer

import Shared.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.N)
class Data {
    private val tag = "Database"

    fun addInstructor(userInfo: HashMap<String, Any>) {
        val userId = userInfo["userId"] as String
        if (!checkIfInstructorExists(userId)) {
            val res = postRequestCreation("instructor/create",userInfo)
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

    fun addParticipant(userInfo: HashMap<String, Any>){
        val userId = userInfo["userId"] as String
        if (!checkIfParticipantExists(userId)) {
            //Add the Participant to the Database
            val res = postRequestCreation("participant/create",userInfo)
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
        var res = ""
        val thread = Thread {
            try {
            res = postRequest("lesson/addUser", lessonInfo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
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
        var res = ""
        val thread = Thread {
            try {
            res = postRequest("lesson/removeUser", lessonInfo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
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





    fun getAvailability(userId: String, date:String,
                        callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String,
                        inLesson : Boolean,addLesson: (flag:Boolean)-> Unit,year:String,lessonInfo:String) -> Unit) {
        var res = ""

        val thread = Thread {
            try {
                res = getRequest("lesson/availability", hashMapOf(
                    "userId" to userId,
                    "date" to date
                ))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
//        }
//        while (wait){}

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
                            lessonDate.split("_")[0],
                            lesson.description
                        )


                        startIdentity+=100
                        layoutId+=100
                    }
                }

            }
        }



    fun validateLesson(
        userId: String,
        lessonInfo: HashMap<String, Any>,
        callback: (message: String) -> Unit
    ): Boolean {
        lessonInfo["userId"] = userId

        var pop = true
        val thread = Thread {
            try {
            pop = postRequestValidate("instructor/addLesson", lessonInfo) == "true"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
        if (pop){
            callback("added successfully")
        }

        else{
            callback("Couldn't add successfully")
        }

        return pop

    }



    fun getInstructorTimeFromDatabase(userId: String, date:String,
                                      callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String) -> Unit){

        var res = ""
        val thread = Thread {
            try {
            res = getRequest("instructor/date", hashMapOf(
                    "userId" to userId,
                    "date" to date
                ))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
        val lessons = Gson().fromJson(res,Array<instructorLesson>::class.java)
        lessons.forEach { field ->
            var startIdentity = 300000
            var layoutId = 400000
            val splitDate = field.date.split("_")
            val lesson = field.lesson
            callback(
                splitDate[1], startIdentity, layoutId,
                "${lesson.ParticipantsList.size}/${lesson.numberOfParticipants}",
                lesson.lessonName,
                lesson.level,
                "${lesson.price * lesson.ParticipantsList.size}$"
            )
            startIdentity += 100
            layoutId += 100
        }

        }


    fun getInstructorStats(map: HashMap<String, Any>, callback: (stats: instructorStats) -> Unit) {
        var res = ""
        val thread = Thread {
            try {
            res = getRequest("instructor/stats", map)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
        val stats = Gson().fromJson(res, instructorStats::class.java)
        callback(stats)
    }

    fun deleteLesson(map:HashMap<String,Any>,callback: (message: String) -> Unit){
        var res = ""
        val thread = Thread {
            try {
                res = getRequest("lessons/delete", map)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()

        if(res == "true"){
            callback("Deleted Successfully")
        }
        else{
            callback("Couldn't delete lesson")
        }
    }

    fun participantLessonFilter(filter: HashMap<String,Any>,userId: String,
                                callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String,
                                           inLesson : Boolean,addLesson: (flag:Boolean)-> Unit,year:String,lessonInfo: String) -> Unit) {
        var res = ""
        val thread = Thread {
            try {
                res = post("lessons/search", filter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
        val lessons = Gson().fromJson(res,Array<fullLesson>::class.java)
        var startIdentity = 300000
        var layoutId = 400000
        for(fulllesson in lessons){
            val lessonDate = fulllesson.date
            val lesson = fulllesson.lesson
            if (lesson.ParticipantsList.size < lesson.numberOfParticipants) {
                    val inList = userId in lesson.ParticipantsList

                    callback(
                        lessonDate.replace('_' ,' '),
//                        lessonDate.split("_")[1],
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
                        lessonDate.split("_")[0],
                        lesson.description
                    )


                    startIdentity+=100
                    layoutId+=100
                }


        }
    }

    fun getUpcomingLessons(map: HashMap<String, Any>, callback: (lesson: List<fullLesson>) -> Unit) {
        var res = ""
        val thread = Thread {
            try {
                res = getRequest("lessons/upcoming", map)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
        val lessons = Gson().fromJson(res,Array<fullLesson>::class.java)
        callback(lessons.toList())
    }

    fun getHistoryLessons(map: HashMap<String, Any>, callback: (lesson: List<fullLesson>) -> Unit) {
        var res = ""
        val thread = Thread {
            try {
                res = getRequest("lessons/history", map)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
        val lessons = Gson().fromJson(res,Array<fullLesson>::class.java)
        callback(lessons.toList())
    }


}