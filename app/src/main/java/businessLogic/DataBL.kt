package businessLogic

import com.google.gson.Gson
import Shared.Lesson
import dataAccessLayer.Data

class DataBL {
    val data = Data()


    suspend fun addInstructor(
        userId: String, firstName: String,
        lastName: String, workPlace: String
    ){
            // Construct User information
            val user:HashMap<String,Any> = hashMapOf(
                "userId" to userId,
                "firstName" to firstName,
                "lastName" to lastName,
                "workPlace" to workPlace,
            )

            data.addInstructor(user)
    }
    suspend fun addParticipant(userId: String, firstName: String,
                      lastName: String){
        val user:HashMap<String,String> = hashMapOf(
            "userId" to userId,
            "firstName" to firstName,
            "lastName" to lastName
        )

        data.addParticipant(user)
    }

    suspend fun checkIfInstructorExists(userId: String): Boolean {
        return data.checkIfInstructorExists(userId)
    }

    suspend fun checkIfParticipantExists(userId: String): Boolean {
        return data.checkIfParticipantExists(userId)
    }



    fun getInstructorTimeFromDatabase(userId: String,date:String,
                                      callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,revenue: String) -> Unit){
        data.getInstructorTimeFromDatabase(userId, date, callback)

    }



    fun addLesson(userId: String,fullDate: String, lesson:Lesson,callback: (message:String) -> Unit) {

        val jsonString = Gson().toJson(lesson)
        val lessonInfo:HashMap<String,Any> = hashMapOf(
            fullDate to jsonString
        )
        data.validateLesson(userId,lessonInfo,callback)
    }



}