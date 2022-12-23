package businessLogic

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
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
    fun addParticipant(userId: String, firstName: String,
                      lastName: String){
        val user:HashMap<String,String> = hashMapOf(
            "userId" to userId,
            "firstName" to firstName,
            "lastName" to lastName
        )

        data.addParticipant(user)
    }

    suspend fun checkIfUserExists(userId: String): Boolean {
        return data.checkIfInstructorExists(userId)
    }

    fun setInstructorTimeInDataBase(userId: String,date:String,hours: String){

        val userHours:HashMap<String,Any> = HashMap()

        userHours[userId] = hours
        data.setInstructorTimeInDataBase(userHours, date)

    }

    fun getInstructorTimeInDataBase(userId: String, date:String,callback:(res:String)-> Unit){
        data.getInstructorTimeInDataBase(userId, date, callback)

    }

    data class Lesson(@SerializedName("lessonName") val lessonName: String,
                      @SerializedName("maxNumberOfParticipants") val numberOfParticipants: Int,
                      @SerializedName("level") val level: String,
                      @SerializedName("price") val price: Double,
                      @SerializedName("description") val description: String,
                      @SerializedName("currentNumberOfParticipants") val currentNumberOfParticipants: Int = 0)

    fun addLesson(userId: String,date: String,
                  time: String,lessonName:String,
                  maxNumberOfParticipants: Int, level: String,price: Double, description: String): Boolean {
        val arr = arrayOf("A", "B", "C", "ALL")
        if (level !in arr || price < 0 || maxNumberOfParticipants < 1) {
            return false
        }
        val field = "${date}_${time}"

        val lesson = Lesson(lessonName,maxNumberOfParticipants,level,price,description)
        val jsonString = Gson().toJson(lesson)
        val lessonInfo:HashMap<String,Any> = hashMapOf(
            field to jsonString
        )
        data.addLesson(userId,lessonInfo)

        return true
    }


}