package dataAccessLayer

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

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
            Log.w(tag,  "Already exists")
        }
    }

    fun addParticipant(userInfo: HashMap<String, String>){

        //Add the Participant to the Database
        db.collection("Participants")
            .add(userInfo)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Added participant with ID:  ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)
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


    fun setInstructorTimeInDataBase(userInfo: HashMap<String, Any>,date: String){
        db.collection("Lessons").document(date).set(userInfo, SetOptions.merge()).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                Log.d(tag,"Added to database")
            }
            else{
                Log.d(tag,"Couldn't modify hours")
            }
        }
    }

    fun getInstructorTimeInDataBase(userId: String, date:String,callback:(res:String)-> Unit){
        db.document("Lessons/${date}").get().addOnCompleteListener{Task ->
            if (Task.isSuccessful){
                val res = Task.result.data?.get(userId).toString()
                Log.d(tag,res)
                callback(res)
            }
            else{
                Log.d(tag,"Couldn't retrieve data")
            }
        }
//            .addOnCompleteListener {Task ->
//            if (Task.isSuccessful){
//                Log.d(tag, "yanir ${Task.result.documents}")
//            }
//
//        }
    }



}