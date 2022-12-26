package com.example.yogatime.Auth

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import businessLogic.DataBL
import com.example.yogatime.Instructor.InstructorDiaryWeekly
import com.example.yogatime.Instructor.PostLoginInstructorActivity
import com.example.yogatime.Participant.ParticipantDiaryWeekly
import com.example.yogatime.Participant.PostLoginParticipantActivity
import com.example.yogatime.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class SignUp : AppCompatActivity() {
    private val tag = "SignUp"
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the userID either through local storage or from application transfer
        var userId = loadUser()
        if(userId==null) {
            userId = this.intent?.getSerializableExtra("userId", String::class.java)
        }
        val dataBL = DataBL()
        val act = this
        val scope = CoroutineScope(newSingleThreadContext("Transfer to right page"))
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
        }


        // Checks if the user is already entered his information , if so transfer it into the right page (PostLoginInstructor)

        Log.d(tag,"UserId $userId")
        setContentView(R.layout.signup)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)

        // Radio button to choose whether you are instructor or participant
        val enterButton = findViewById<MaterialButton>(R.id.btn_enter)
        enterButton.setOnClickListener{
            // Get the checked radio button id from radio group
            val id: Int = radioGroup.checkedRadioButtonId
            if (id!=-1){ // If any radio button checked from radio group
                // Get the instance of radio button using id
                val radio:RadioButton = findViewById(id)
                Toast.makeText(applicationContext,
                        " ${radio.text}",
                    Toast.LENGTH_SHORT).show()
                if (userId != null) {
                    if (radio.text == "Yoga Instructor") {
                        postLoginInstructor(userId)
                    }
                    else {
                        postLoginParticipant(userId)
                    }
                }
            }else{
                // If no radio button checked in this radio group
                Toast.makeText(applicationContext, "Nothing Selected",
                    Toast.LENGTH_SHORT).show()
            }
        }


    }
    // Load user from local storage
    private fun loadUser(): String? {
        val sharedPref = getSharedPreferences("sharedUser",Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }
   // Post login activity after entering information
    private fun postLoginInstructor(userId: String) {
        val intent = Intent(this, PostLoginInstructorActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
    private fun postLoginParticipant(userId: String){
        val intent = Intent(this, PostLoginParticipantActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }




}