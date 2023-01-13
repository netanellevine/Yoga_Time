package com.example.yogatime.Participant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.yogatime.Instructor.InstructorDiaryWeekly
import com.example.yogatime.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ParticipantProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participant_profile)


        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.personal_profile_participant_nav
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.personal_profile_participant_nav -> {
                    true
                }
                R.id.schedule_participant_nav -> {
                    startActivity(Intent(this, ParticipantDiaryWeekly::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}