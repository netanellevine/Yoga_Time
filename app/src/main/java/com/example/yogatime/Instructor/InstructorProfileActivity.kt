package com.example.yogatime.Instructor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.yogatime.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class InstructorProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_profile)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.personal_profile_instructor_nav
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.personal_profile_instructor_nav -> {
                    true
                }
                R.id.schedule_instructor_nav -> {
                    startActivity(Intent(this, InstructorDiaryWeekly::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

    }
}