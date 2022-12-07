package com.example.yogatime

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val intent = Intent(this, StartActivity::class.java)
        // start your next activity
//        Log.d("Transfer userid",userId)
//        intent.putExtra("userId",userId)
        startActivity(intent)
    }

}
