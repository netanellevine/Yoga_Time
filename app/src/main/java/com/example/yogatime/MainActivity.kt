package com.example.yogatime

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.yogatime.Instructor.InstructorProfileActivity


class MainActivity : AppCompatActivity() {

    /**
     * This functions transfers to the start activity
     */

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val intent = Intent(this, StartActivity::class.java)
        val intent = Intent(this, InstructorProfileActivity::class.java)
        startActivity(intent)
    }


}
