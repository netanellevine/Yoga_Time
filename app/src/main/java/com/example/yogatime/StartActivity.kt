package com.example.yogatime

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
/**
 * If the user is already logged in we transfer it into the Signup page
 */
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        // check if the userid is null else transfer it to the signup page
        if (loadUser()!=null){
            Log.d("user","user id is not null")
            val intent = Intent(this, SignUp::class.java)
            // start your next activity

            startActivity(intent)
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