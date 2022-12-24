package com.example.yogatime.Auth

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import businessLogic.AuthBL
import com.example.yogatime.R

class OTPVerificationActivity(context: Context, authBL: AuthBL, mobile: String) : Dialog(context) {

    // Declare on variables
    private var mobile: String
    private var authBL: AuthBL
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText
    private lateinit var btn_verify: Button
    private lateinit var btn_resend: TextView
    private val TAG: String = "OTPVerificationActivity"
    private var resendEnabled: Boolean = false
    private val resendTimer: Int = 60
    private lateinit var selectedEditText: EditText

    init {
        this.mobile = mobile
        this.authBL = authBL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.activity_otpverification)

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otp5 = findViewById(R.id.otp5)
        otp6 = findViewById(R.id.otp6)
        btn_verify = findViewById(R.id.otp_verify_button)
        btn_resend = findViewById(R.id.otp_resend)
        val mobileNumber = findViewById<TextView>(R.id.otp_mobile_number)
        selectedEditText = otp1

        btn_verify.isEnabled = false

        otp1.addTextChangedListener(textWatcher)
        otp2.addTextChangedListener(textWatcher)
        otp3.addTextChangedListener(textWatcher)
        otp4.addTextChangedListener(textWatcher)
        otp5.addTextChangedListener(textWatcher)
        otp6.addTextChangedListener(textWatcher)

        // By default, open keyboard when dialog is shown and focus on first EditText
        showKeyboard(otp1)

        // Start timer for resend button
        startCountDownTimer()

        // Set mobile number
        mobileNumber.text = mobile

        btn_resend.setOnClickListener {
            Log.d(TAG, "Clicked resend")
            if (resendEnabled) {
                Log.d(TAG, "Resend OTP")
                authBL.resendCode()
                startCountDownTimer()
            }
        }

        btn_verify.setOnClickListener {
            Log.d(TAG, "Clicked verify")
            val otp =
                otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString()
            if (otp.length == 6) {
                Log.d(TAG, "Verify OTP")
                authBL.verify(otp)

            }
        }

    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.isNotEmpty()) {
                when (selectedEditText) {
                    otp1 -> {
                        selectedEditText = otp2
                        showKeyboard(otp2)
                    }
                    otp2 -> {
                        selectedEditText = otp3
                        showKeyboard(otp3)
                    }
                    otp3 -> {
                        selectedEditText = otp4
                        showKeyboard(otp4)
                    }
                    otp4 -> {
                        selectedEditText = otp5
                        showKeyboard(otp5)
                    }
                    otp5 -> {
                        selectedEditText = otp6
                        showKeyboard(otp6)
                    }
                    otp6 -> {
                        btn_verify.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showKeyboard(otp: EditText) {
        otp.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(otp, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun startCountDownTimer() {
        resendEnabled = false
        btn_resend.isEnabled = false
        btn_resend.text = resendTimer.toString()
        val countDownTimer = object : CountDownTimer((resendTimer * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                btn_resend.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                resendEnabled = true
                btn_resend.isEnabled = true
                btn_resend.text = "Resend"
            }
        }
        countDownTimer.start()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            when (selectedEditText) {
                otp6 -> {
                    selectedEditText = otp5
                    showKeyboard(otp5)
                }
                otp5 -> {
                    selectedEditText = otp4
                    showKeyboard(otp4)
                }
                otp4 -> {
                    selectedEditText = otp3
                    showKeyboard(otp3)
                }
                otp3 -> {
                    selectedEditText = otp2
                    showKeyboard(otp2)
                }
                otp2 -> {
                    selectedEditText = otp1
                    showKeyboard(otp1)
                }
            }

            btn_verify.isEnabled = false
            return true
        }
        return super.onKeyUp(keyCode, event)
    }
}