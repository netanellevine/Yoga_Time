package com.example.yogatime.Participant

import Shared.fullLesson
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TableLayout
import android.widget.TableRow
import androidx.annotation.RequiresApi
import businessLogic.DataBL
import com.example.yogatime.Instructor.InstructorDiaryWeekly
import com.example.yogatime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textview.MaterialTextView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ParticipantProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var dataBL: DataBL
    private val tag = "ParticipantProfile"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participant_profile)

        var userId = loadUser()
        if (userId == null) {
            userId = this.intent?.getSerializableExtra("userId") as String
        }

        dataBL = DataBL()

        dataBL.getUpcomingParticipantLessons(
            userId!!,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd_HH:mm")),
            5,
            ::updateUpcomingLessons)

        dataBL.getHistoryParticipantLessons(
            userId!!,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd_HH:mm")),
            5,
            ::updateHistoryLessons)

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


    fun updateUpcomingLessons(stats: List<fullLesson>) {
        val upcomingTable = findViewById<TableLayout>(R.id.participant_upcoming_lessons_tablelayout)
//        upcomingTable.removeAllViews()

        for (lesson in stats) {
            val row =
                LayoutInflater.from(this).inflate(R.layout.participant_upcoming_lessons_row, null) as TableRow
            val dateTextView = row.findViewById<MaterialTextView>(R.id.participant_upcoming_lesson_date)
            val nameTextView = row.findViewById<MaterialTextView>(R.id.participant_upcoming_lesson_name)
            val priceTextView = row.findViewById<MaterialTextView>(R.id.participant_upcoming_lesson_price)
            dateTextView.text = lesson.date
            nameTextView.text = lesson.lesson.lessonName
            priceTextView.text = lesson.lesson.price.toString()
            upcomingTable.addView(row)
        }
        upcomingTable.requestLayout()
    }

    fun updateHistoryLessons(stats: List<fullLesson>) {
        val historyTable = findViewById<TableLayout>(R.id.participant_history_lessons_tablelayout)
//        historyTable.removeAllViews()

        for (lesson in stats) {
            val row =
                LayoutInflater.from(this).inflate(R.layout.participant_history_lessons_row, null) as TableRow
            val dateTextView = row.findViewById<MaterialTextView>(R.id.participant_history_lesson_date)
            val nameTextView = row.findViewById<MaterialTextView>(R.id.participant_history_lesson_name)
            val revenueTextView = row.findViewById<MaterialTextView>(R.id.participant_history_lesson_price)

            dateTextView.text = lesson.date
            nameTextView.text = lesson.lesson.lessonName
            revenueTextView.text = lesson.lesson.price.toString()
            historyTable.addView(row)
        }
        historyTable.requestLayout()
    }


    private fun loadUser(): String? {
        val sharedPref = getSharedPreferences("sharedUser", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }
}