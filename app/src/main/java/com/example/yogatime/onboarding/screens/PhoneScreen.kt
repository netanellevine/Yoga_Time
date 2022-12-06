package com.example.yogatime.onboarding.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import businessLogic.AuthBL
import com.example.yogatime.R
import java.util.logging.Logger

class PhoneScreen : Fragment() {

    lateinit private var auth: AuthBL
    val TAG: String = "test"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone_screen, container, false)
        val viewPager =
            activity?.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)

        val btn_next =
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_enter)

        val countryCodePicker =
            view.findViewById<com.hbb20.CountryCodePicker>(R.id.countryCodePicker)

        val phoneNumber =
            view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.phoneNumber)

        countryCodePicker.registerCarrierNumberEditText(phoneNumber)
        btn_next.setOnClickListener {
            viewPager?.currentItem = 2

//            if (countryCodePicker.isValidFullNumber) {
//                val number = countryCodePicker.fullNumberWithPlus
//                auth = AuthBL(number, this.requireActivity() as AppCompatActivity)
//                auth.authentication(number, this.activity)
//                Log.d("TAG", "onCreateView: $number")
//            } else {
//                phoneNumber.error = "Invalid Number"
//                Log.w("TAG", "onCreateView: Invalid Number")
//            }


        }


        return view
    }

}