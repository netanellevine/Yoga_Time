package com.example.yogatime.Instructor

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
import com.example.yogatime.R
import com.example.yogatime.Auth.SignUp
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import kotlinx.coroutines.*


class PostLoginInstructorActivity : AppCompatActivity() {
    lateinit var databl :DataBL
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_signup)
        // Get the userId from previous activity
        val user = this.intent?.getSerializableExtra("userId") as String
        var userId: String? = null
        if (user != null) {
            userId = user as String
        }
        if (userId != null) {
            Log.d("getUserId",userId)
        }

        val phoneNumber = this.intent?.getSerializableExtra("PhoneNumber"/*, String::class.java*/) as String
        if (phoneNumber != null) {
            Log.d("phoneNumber",phoneNumber)
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

            val workPlace: EditText = findViewById(R.id.workPlace)
            val workPlaceText = workPlace.text.toString()

            // Add the instructor to the database
            run {
                val scope = CoroutineScope(newSingleThreadContext("Add instructor"))
                scope.launch {
                    databl.addInstructor(
                        userId = userId!!, firstName = firstNameText,
                        lastName = lastNameText,
                        workPlace = workPlaceText,
                        PhoneNumber = phoneNumber!!
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