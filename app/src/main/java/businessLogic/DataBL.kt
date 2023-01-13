package businessLogic

import com.google.gson.Gson
import Shared.Lesson
import android.os.Build
import androidx.annotation.RequiresApi
import dataAccessLayer.Data
@RequiresApi(Build.VERSION_CODES.N)
class DataBL {
    val data = Data()



    suspend fun addInstructor(
        userId: String, firstName: String,
        lastName: String, workPlace: String,
        PhoneNumber: String
    ){
            // Construct User information
            val user:HashMap<String,Any> = hashMapOf(
                "userId" to userId,
                "firstName" to firstName,
                "lastName" to lastName,
                "workPlace" to workPlace,
                "phoneNumber" to PhoneNumber
            )

            data.addInstructor(user)
    }
    suspend fun addParticipant(userId: String, firstName: String,
                      lastName: String,PhoneNumber: String){
        val user:HashMap<String,Any> = hashMapOf(
            "userId" to userId,
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to PhoneNumber
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
                                      callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String) -> Unit){
        data.getInstructorTimeFromDatabase(userId, date, callback)

    }

    fun getAvailability(userId: String,date:String,
                        callback: (hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String,
                                   inLesson : Boolean,addLesson: (flag:Boolean)-> Unit,year:String,lessonInfo: String) -> Unit) {
        data.getAvailability(userId,date, callback)

    }



    fun addLesson(userId: String,fullDate: String, lesson:Lesson,callback: (message:String) -> Unit) {

        val jsonString = Gson().toJson(lesson)
        val lessonInfo:HashMap<String,Any> = hashMapOf(
            "key" to fullDate,
            "lesson" to jsonString
        )
        data.validateLesson(userId,lessonInfo,callback)
    }





}