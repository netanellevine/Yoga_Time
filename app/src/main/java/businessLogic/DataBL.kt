package businessLogic

import dataAccessLayer.Data

class DataBL {
    val data = Data()


    suspend fun addInstructor(
        userId: String, firstName: String,
        lastName: String, workPlace: String,
        price: Number, numberOfPeoplePerLesson: Int
    ){
            // Construct User information
            val user:HashMap<String,Any> = hashMapOf(
                "userId" to userId,
                "firstName" to firstName,
                "lastName" to lastName,
                "workPlace" to workPlace,
                "price" to price,
                "numberOfPeoplePerLesson" to numberOfPeoplePerLesson
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


}