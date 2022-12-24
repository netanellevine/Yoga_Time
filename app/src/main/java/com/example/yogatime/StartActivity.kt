package com.example.yogatime

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import businessLogic.DataBL
import com.example.yogatime.Auth.OTPActivity
import com.example.yogatime.Auth.SignUp
import com.example.yogatime.Instructor.InstructorDiaryWeekly
import com.example.yogatime.Participant.ParticipantDiaryWeekly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

/**
 * If the user is already logged in we transfer it into the Signup page
 */
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val dataBL = DataBL()
        // check if the userid is null else transfer it to the signup page
        val userId = loadUser()
        val act = this
        if (loadUser()!=null){
            Log.d("user","user id is not null")
            val scope = CoroutineScope(newSingleThreadContext("Add instructor"))
            scope.launch {
                if(userId?.let { dataBL.checkIfInstructorExists(it) } == true) {
                    val intent = Intent(act, InstructorDiaryWeekly::class.java)
                    // start your next activity
                    Log.d("Transfer userid",userId)
                    intent.putExtra("userId",userId)
                    startActivity(intent)
                }

                else if(userId?.let { dataBL.checkIfParticipantExists(it) } == true) {
                    val intent = Intent(act, ParticipantDiaryWeekly::class.java)
                    // start your next activity
                    Log.d("Transfer userid",userId)
                    intent.putExtra("userId",userId)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(act, SignUp::class.java)
                    startActivity(intent)
                }
            }


        }
        // View the button and lets go
        else {
            Log.d("user","user id is  null")
            findViewById<Button>(R.id.btn_next).setOnClickListener {
                val intent = Intent(this, OTPActivity::class.java)
                // start your next activity

                startActivity(intent)
            }
        }
    }

    /**
     * Load the user from local data
     */
    private fun loadUser(): String? {
        val sharedPref = getSharedPreferences("sharedUser",Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }
}