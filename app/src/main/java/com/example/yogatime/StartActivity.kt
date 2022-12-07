package com.example.yogatime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


        findViewById<Button>(R.id.btn_next).setOnClickListener {
            val intent = Intent(this, OTPActivity::class.java)
            // start your next activity

            startActivity(intent)
        }
    }
}