package com.example.yogatime.Participant

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import businessLogic.DataBL
import com.example.yogatime.Instructor.InstructorDiaryWeekly
import com.example.yogatime.R
import com.example.yogatime.Auth.SignUp
import kotlinx.coroutines.*


class PostLoginParticipantActivity : AppCompatActivity() {
    lateinit var databl :DataBL
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participant_signup)
        // Get the userId from previous activity
        val userId = this.intent?.getSerializableExtra("userId", String::class.java)
        if (userId != null) {
            Log.d("getUserId",userId)
        }
        // initialize Databl
        databl = DataBL()
        val act = this

        // Back to signup button in case of mistake
        val backButton: ImageButton = findViewById(R.id.backToSignUp)
        backButton.setOnClickListener {
            val intent = Intent(act, SignUp::class.java)
            // start your next activity
            if (userId != null) {
                Log.d("Transfer userid",userId)
            }
            intent.putExtra("userId",userId)
            startActivity(intent)
        }



        // Enter info button receives 3 inputs
        // First Name
        // Last Name
        // Work place
        // Enter the variables to the database
        val enterInfo = findViewById<Button>(R.id.info)
        enterInfo.setOnClickListener{
            val firstName: EditText = findViewById(R.id.firstName)
            val firstNameText = firstName.text.toString()

            val lastName: EditText = findViewById(R.id.lastName)
            val lastNameText = lastName.text.toString()



            // Add the instructor to the database
            run {
                val scope = CoroutineScope(newSingleThreadContext("Add instructor"))
                scope.launch {
                    databl.addParticipant(
                        userId = userId!!, firstName = firstNameText,
                        lastName = lastNameText,
                    )
                    val intent = Intent(act, InstructorDiaryWeekly::class.java)
                    // start your next activity
                    Log.d("Transfer userid",userId)
                    intent.putExtra("userId",userId)
                    startActivity(intent)
                }
            }


        }

    }
}