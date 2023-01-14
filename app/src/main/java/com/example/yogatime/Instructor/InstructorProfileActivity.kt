package com.example.yogatime.Instructor

import Shared.instructorStats
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import businessLogic.DataBL
import com.example.yogatime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.core.View
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
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
        if (userId != null) {
            userId = this.intent?.getSerializableExtra("userId") as String
        }

        dataBL = DataBL()

        val items = resources.getStringArray(R.array.overview_span)
        val adapter = ArrayAdapter(this, R.layout.list_item_exposeddropdown, items)
        val textField: AutoCompleteTextView = findViewById(R.id.overview_span_dropdown)


        textField.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            if (item == currentOverview) {
                return@setOnItemClickListener
            }
            Log.d(tag, "Selected item: $item")
            var endDate : LocalDate? = null
            var startDate : LocalDate? = null
            when (item) {
                "All Time" -> {
                    startDate = LocalDate.MIN
                    endDate = LocalDate.MAX
                }
                "Today" -> {
                    startDate = LocalDate.now()
                    endDate = LocalDate.now()
                }
                "This Week" -> {
                    startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    endDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

                }
                "This Month" -> {
                    startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
                    endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
                }
            }
            dataBL.getInstructorStats(userId!!, startDate!!.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), endDate!!.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), ::updateStats)
            currentOverview = item
        }
        currentOverview = items[0]
        textField.setText(currentOverview, false)
        textField.setAdapter(adapter)
//        val startDate = LocalDate.MIN
//        val endDate = LocalDate.MAX
//        dataBL.getInstructorStats(userId!!, startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), endDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), ::updateStats)


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
            val textView = findViewById<MaterialAutoCompleteTextView>(id)
            textView.setText(element.value.toString())
        }

    }



    private fun loadUser(): String? {
        val sharedPref = getSharedPreferences("sharedUser", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }

}