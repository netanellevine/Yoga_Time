package com.example.yogatime.Auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import businessLogic.AuthBL
import com.example.yogatime.R


class OTPActivity : AppCompatActivity() {

    private lateinit var auth: AuthBL
    val tag: String = "Authentication"
    // Authentication page, In this activity you can authenticate into our application
    // In order to authenticate we enter the phone number, that is it.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)


        // Initialize variables
        val btnNext =
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_enter)

        val countryCodePicker =
            findViewById<com.hbb20.CountryCodePicker>(R.id.countryCodePicker)

        val phoneNumber =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.phoneNumber)

        // Get the phone number
        countryCodePicker.registerCarrierNumberEditText(phoneNumber)

        btnNext.setOnClickListener {
            // Check if the phone number is valid
            if (countryCodePicker.isValidFullNumber) {
                // If the phone number is valid we send a code
                val number = countryCodePicker.fullNumberWithPlus
                auth = AuthBL(number, this)
                auth.authenticate { userId -> postLogin(userId, number) }
                Log.d(tag, "onCreateView: $number")
                // Popup to receive the code
                val otp : OTPVerificationActivity = OTPVerificationActivity(this,auth,number)
                otp.setCancelable(false)
                otp.show()



            } else {
                phoneNumber.error = "Invalid Number"
                Log.w("TAG", "onCreateView: Invalid Number")
            }


        }


    }

    // Transfer to post login page, this is a callback function which we transfer
    private fun postLogin(userId: String,PhoneNumber:String) {
        val intent = Intent(this, SignUp::class.java)
        val shared = getSharedPreferences("sharedUser",Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.apply {
            putString("userId",userId)
            putString("PhoneNumber",PhoneNumber)
        }.apply()
        intent.putExtra("userId", userId)
        intent.putExtra("PhoneNumber", PhoneNumber)
        startActivity(intent)
    }

    // Load the user id

}