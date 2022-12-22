package com.example.yogatime

import android.content.res.Resources
import android.graphics.Color
import android.os.Build

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi


import androidx.appcompat.app.AppCompatActivity

import com.kizitonwose.calendar.core.*

import com.kizitonwose.calendar.view.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import java.util.*


class InstructorDiaryWeekly: AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        val width = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instructor_weekly)
        val userId: String? = intent.getSerializableExtra("userId",String::class.java)

        val weekCalendarView: WeekCalendarView = findViewById(R.id.weekCalendarView)
        val red = Color.RED
        val white = Color.WHITE
        var markedContainer: DayViewContainer? = null
        weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: WeekDay) {
                val formatter = DateTimeFormatter.ofPattern("MM-dd")
                container.textView.text = data.date.dayOfWeek.toString().substring(0,3) + '\n' +"${data.date.format(formatter)}"
                container.textView.textSize = width/100
                container.textView.setTextColor(red)
                container.view.setBackgroundColor(white)
                container.view.setOnClickListener(object : DoubleClickListener() {
                    override fun onDoubleClick(v: View) {
                        changeColor(container,red,white)
                        changeColor(markedContainer,white,red)
                        markedContainer = container
                    }
                })
            }
        }
        var currentDate = LocalDate.now()
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(100).atStartOfMonth() // Adjust as needed
        val endDate = currentMonth.plusMonths(100).atEndOfMonth()  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
        weekCalendarView.scrollToWeek(currentDate)

        @RequiresApi(Build.VERSION_CODES.O)
        fun changeDate(imageView: ImageView, flag:Boolean) {
            if (flag){
                imageView.setOnClickListener{
                    currentDate = currentDate.plusWeeks(1)

                    weekCalendarView.scrollToWeek(currentDate)
                }
            }
            else{
                imageView.setOnClickListener{
                    currentDate = currentDate.plusWeeks(-1)
                    weekCalendarView.scrollToWeek(currentDate)
                }
            }

        }
        changeDate(findViewById(R.id.forward),true)
        changeDate(findViewById(R.id.backward),false)





    }



    abstract class DoubleClickListener : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
                lastClickTime = 0
            }
            lastClickTime = clickTime
        }
        abstract fun onDoubleClick(v: View)
        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }
    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
        // With ViewBinding
        // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    }
    fun changeColor(container: DayViewContainer?,color1:Int,color2:Int){
        container?.view?.setBackgroundColor(color1)
        container?.textView?.setTextColor(color2)

    }

    fun addTable(){

    }

}