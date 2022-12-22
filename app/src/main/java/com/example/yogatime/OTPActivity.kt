package com.example.yogatime

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import businessLogic.AuthBL


class OTPActivity : AppCompatActivity() {

    private lateinit var auth: AuthBL
    val tag: String = "Authentication"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)



        val btnNext =
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_enter)

        val countryCodePicker =
            findViewById<com.hbb20.CountryCodePicker>(R.id.countryCodePicker)

        val phoneNumber =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.phoneNumber)

        countryCodePicker.registerCarrierNumberEditText(phoneNumber)

        btnNext.setOnClickListener {

            if (countryCodePicker.isValidFullNumber) {
                val number = countryCodePicker.fullNumberWithPlus
                auth = AuthBL(number, this)
                auth.authenticate(::postLogin)
                Log.d(tag, "onCreateView: $number")
                val otp : OTPVerificationActivity = OTPVerificationActivity(this,auth,number)
                otp.setCancelable(false)
                otp.show()
//
//                var text: String
//
//                val builder = AlertDialog.Builder(this)
//                builder.setTitle("Please Enter Your Verification Code")
//
//// Set up the input
//
//// Set up the input
//                val input = EditText(this)
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.inputType = InputType.TYPE_CLASS_NUMBER
//                input.setBackgroundColor(Color.BLACK)
//                input.setTextColor(Color.WHITE)
//                input.gravity = Gravity.CENTER_HORIZONTAL
//
//                builder.setView(input)
//
//// Set up the buttons
//
//// Set up the buttons
//                builder.setPositiveButton(
//                    "OK"
//                ) { _, _ -> text = input.text.toString()
//                    auth.verify(text)}
//                builder.setNegativeButton(
//                    "Wrong phone number"
//                ) { dialog, _ -> dialog.cancel() }
//                builder.setNeutralButton("Resend code")
//                {_ , _ -> auth.resendCode()
//                    val resend = AlertDialog.Builder(this)
//                    resend.setTitle("Please Enter Your Verification Code")
//                    val resend_input = EditText(this)
//                   resend_input.inputType = InputType.TYPE_CLASS_NUMBER
//                    resend_input.setBackgroundColor(Color.BLACK)
//                    resend_input.setTextColor(Color.WHITE)
//                    resend_input.gravity = Gravity.CENTER_HORIZONTAL
//                    resend.setView(resend_input)
//                    resend.setPositiveButton(
//                        "OK"
//                    ) { _, _ -> text = resend_input.text.toString()
//                        auth.verify(text)}
//                    resend.setNegativeButton(
//                        "Wrong phone number"
//                    ) { dialog, _ -> dialog.cancel() }
//                    resend.show()
//                }
//                builder.setIcon(R.drawable.button_bg1)
//                builder.show()


            } else {
                phoneNumber.error = "Invalid Number"
                Log.w("TAG", "onCreateView: Invalid Number")
            }


        }


    }

    // Transfer to post login
    private fun postLogin(userId: String) {
        val intent = Intent(this, SignUp::class.java)
        val shared = getSharedPreferences("sharedUser",Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.apply {
            putString("userId",userId)
        }.apply()
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    // Load the user id

}