package com.example.yogatime

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import businessLogic.AuthBL
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class OTPActivity : AppCompatActivity() {

    lateinit private var auth: AuthBL
    val TAG: String = "test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)
//        val userId = loadUser()
//        if (userId != null) {
//            postLogin(userId)
//        }

        val btn_next =
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_enter)

        val countryCodePicker =
            findViewById<com.hbb20.CountryCodePicker>(R.id.countryCodePicker)

        val phoneNumber =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.phoneNumber)

        countryCodePicker.registerCarrierNumberEditText(phoneNumber)

        btn_next.setOnClickListener {

            if (countryCodePicker.isValidFullNumber) {
                val number = countryCodePicker.fullNumberWithPlus
                auth = AuthBL(number, this)
                auth.authenticate(::postLogin)
                Log.d("TAG", "onCreateView: $number")

//                MaterialAlertDialogBuilder(this)
//                    .setTitle("Title")
//                    .setMessage("Your message goes here. Keep it short but clear.")
//                    .setPositiveButton(
//                        "GOT IT"
//                    ) { dialogInterface, i -> }
//                    .setNegativeButton(
//                        "CANCEL"
//                    ) { dialogInterface, i -> }
//                    .show()
            } else {
                phoneNumber.error = "Invalid Number"
                Log.w("TAG", "onCreateView: Invalid Number")
            }


        }
//        val ver = findViewById(R.id.VerificationButton) as Button
//        ver.setOnClickListener {
//            val codeText = findViewById(R.id.VerificationCode) as EditText
//            val code = codeText.text.toString()
//            auth.verify(code)
//
//        }
//
//        val res = findViewById(R.id.Resend) as Button
//        res.setOnClickListener {
//            auth.resendCode()
//        }
    }

    // Transfer to post login
    fun postLogin(userId: String): Unit {
//        val intent = Intent(this, PostLoginActivity::class.java)
//        // start your next activity
//        Log.d("Transfer userid", userId)
//        intent.putExtra("userId", userId)
//        startActivity(intent)
    }

    // Load the user id
//    fun loadUser(): String? {
//        val sharedData: SharedPreferences = getSharedPreferences("userId", Context.MODE_PRIVATE)
//
//        return sharedData.getString("userId", null)
//    }
}