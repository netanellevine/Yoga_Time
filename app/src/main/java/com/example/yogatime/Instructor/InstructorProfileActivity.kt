package com.example.yogatime.Instructor

import Shared.fullLesson
import Shared.instructorStats
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TableLayout
import android.widget.TableRow
import androidx.annotation.RequiresApi
import businessLogic.DataBL
import com.example.yogatime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textview.MaterialTextView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class InstructorProfileActivity : AppCompatActivity() {

    private lateinit var dataBL: DataBL
    private val tag = "InstructorProfile"

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var currentOverview: String

    //    @RequiresApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_profile)

        var userId = loadUser()
        if (userId == null) {
            userId = this.intent?.getSerializableExtra("userId") as String
        }

        dataBL = DataBL()

        val items = resources.getStringArray(R.array.overview_span)
        val adapter = ArrayAdapter(this, R.layout.list_item_exposeddropdown, items)
        val textField: AutoCompleteTextView = findViewById(R.id.overview_span_dropdown)
        dataBL.getInstructorStats(
            userId!!,
            LocalDate.now().minusYears(500)!!.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            LocalDate.now().plusYears(500)!!.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            ::updateStats
        )


        textField.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            if (item == currentOverview) {
                return@setOnItemClickListener
            }
            Log.d(tag, "Selected item: $item")
            var endDate: LocalDate? = null
            var startDate: LocalDate? = null
            when (item) {
                "All Time" -> {
                    startDate = LocalDate.now().minusYears(500)
                    endDate = LocalDate.now().plusYears(500)
                }
                "Today" -> {
                    startDate = LocalDate.now()
                    endDate = LocalDate.now()
                }
                "This Week" -> {
                    startDate =
                        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    endDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

                }
                "This Month" -> {
                    startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
                    endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
                }
            }
            dataBL.getInstructorStats(
                userId!!,
                startDate!!.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                endDate!!.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                ::updateStats
            )
            currentOverview = item
        }
        currentOverview = items[0]
        textField.setText(currentOverview, false)
        textField.setAdapter(adapter)
//        val startDate = LocalDate.MIN
//        val endDate = LocalDate.MAX
//        dataBL.getInstructorStats(userId!!, startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), endDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), ::updateStats)

        dataBL.getUpcomingLessons(
            userId!!,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd_HH:mm")),
            5,
            ::updateUpcomingLessons)

        dataBL.getHistoryLessons(
            userId!!,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd_HH:mm")),
            5,
            ::updateHistoryLessons)

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


    fun updateStats(stats: instructorStats) {
        val elements = hashMapOf(
            "avg_participants_textview" to stats.avgParticipants,
            "avg_revenue_textview" to stats.avgRevenue,
            "total_lessons_textview" to stats.totalLessons,
            "total_revenue_textview" to stats.totalRevenue
        )

        for (element in elements) {
            val id = resources.getIdentifier(element.key, "id", packageName)
            val textView = findViewById<MaterialTextView>(id)
            textView.text = element.value.toString()
        }

    }

    fun updateUpcomingLessons(stats: List<fullLesson>) {
        val upcomingTable = findViewById<TableLayout>(R.id.upcoming_lessons_tablelayout)
//        upcomingTable.removeAllViews()

        for (lesson in stats) {
            val row =
                LayoutInflater.from(this).inflate(R.layout.upcoming_lessons_row, null) as TableRow
            val dateTextView = row.findViewById<MaterialTextView>(R.id.upcoming_lesson_date)
            val nameTextView = row.findViewById<MaterialTextView>(R.id.upcoming_lesson_name)
            val revenueTextView = row.findViewById<MaterialTextView>(R.id.upcoming_lesson_revenue)
            dateTextView.text = lesson.date
            nameTextView.text = lesson.lesson.lessonName
            revenueTextView.text = lesson.lesson.price.toString()
            upcomingTable.addView(row)
        }
        upcomingTable.requestLayout()
    }

    fun updateHistoryLessons(stats: List<fullLesson>) {
        val historyTable = findViewById<TableLayout>(R.id.history_lessons_tablelayout)
        historyTable.removeAllViews()

        for (lesson in stats) {
            val row =
                LayoutInflater.from(this).inflate(R.layout.history_lessons_row, null) as TableRow
            val dateTextView = row.findViewById<MaterialTextView>(R.id.history_lesson_date)
            val nameTextView = row.findViewById<MaterialTextView>(R.id.history_lesson_name)
            val revenueTextView = row.findViewById<MaterialTextView>(R.id.history_lesson_revenue)
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