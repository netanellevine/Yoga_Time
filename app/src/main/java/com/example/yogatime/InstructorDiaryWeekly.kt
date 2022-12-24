package com.example.yogatime

import Shared.Lesson
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi


import androidx.appcompat.app.AppCompatActivity
import businessLogic.DataBL
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.kizitonwose.calendar.core.*

import com.kizitonwose.calendar.view.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import java.util.*
import kotlin.properties.Delegates


class InstructorDiaryWeekly: AppCompatActivity(),InstructorLessonPopupFragment.OnForwardListener {
    var userId: String? = null
    var databl = DataBL()
    var width by Delegates.notNull<Float>()
    var height by Delegates.notNull<Float>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize variables
        width = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instructor_weekly)
        userId = loadUser()
        if(userId!= null) {
            userId = intent.getSerializableExtra("userId",String::class.java)
        }

        // Initialize calender view
        val weekCalendarView: WeekCalendarView = findViewById(R.id.weekCalendarView)
        val red = Color.rgb(14,75,84)
        val white = Color.WHITE
        var markedContainer: DayViewContainer? = null


        // Add save popup
        val saveDatePopup = findViewById<FloatingActionButton>(R.id.floating_action_button)

        saveDatePopup.setOnClickListener{
            val instructorLessonPopupFragment = InstructorLessonPopupFragment
            if (userId != null) {
                instructorLessonPopupFragment.display(supportFragmentManager)
            }

        }

        //Initialize the view
        weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: WeekDay) {
                // Build the daily view
                val formatter = DateTimeFormatter.ofPattern("MM-dd")
                val yearFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                container.textView.text = data.date.dayOfWeek.toString().substring(0,3) + '\n' +"${data.date.format(formatter)}"
                container.textView.textSize = width/100
                container.textView.setTextColor(red)
                container.view.setBackgroundColor(white)
                container.view.setOnClickListener(object : DoubleClickListener() {
                    override fun onDoubleClick(v: View) {
                        changeColor(container,red,white)
                        changeColor(markedContainer,white,red)
                        markedContainer = container
                        removeTables()
                        if (markedContainer!=container) {
                            val year = data.date.format(yearFormat)
                            userId?.let {
                                databl.getInstructorTimeFromDatabase(
                                    it,
                                    year,
                                    ::addTable
                                )
                            }
                        }
                    }
                })
            }
        }
        // Scroll to the current date and define max and min dates
        var currentDate = LocalDate.now()
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(100).atStartOfMonth() // Adjust as needed
        val endDate = currentMonth.plusMonths(100).atEndOfMonth()  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
        weekCalendarView.scrollToWeek(currentDate)
        // Function to change the date
        @RequiresApi(Build.VERSION_CODES.O)
        fun changeDate(imageView: ImageView, flag:Boolean) {
            if (flag){
                imageView.setOnClickListener{
                    removeTables()
                    changeColor(markedContainer,white,red)
                    currentDate = currentDate.plusWeeks(1)
                    weekCalendarView.scrollToWeek(currentDate)
                }
            }
            else{
                imageView.setOnClickListener{
                    removeTables()
                    changeColor(markedContainer,white,red)
                    currentDate = currentDate.plusWeeks(-1)
                    weekCalendarView.scrollToWeek(currentDate)
                }
            }

        }
        // Set on click listeners for the buttons
        changeDate(findViewById(R.id.forward),true)
        changeDate(findViewById(R.id.backward),false)





    }


    // On double click listener will remove it later
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
    // Container view add the text to the container
    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
        // With ViewBinding
        // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    }
    // Switch color of a container
    fun changeColor(container: DayViewContainer?,color1:Int,color2:Int){
        container?.view?.setBackgroundColor(color1)
        container?.textView?.setTextColor(color2)

    }
    // Add layout to the table, which we use to present the lesson information
    fun addLayoutToTable(hour:String,height: Float,width: Float,layoutId: Int,startIdentity: Int,currentlySigned: String,lessonName:String,revenue:String){
        val hourView = createTextView(text=hour, height = height/2, width = width, toDraw = true)

        val layout = createLayout(identitiy = layoutId)
        findViewById<LinearLayout>(R.id.timeLayout).addView(layout)




        layout.addView(hourView)

        val peopleNumberLayout = createLayout(false,layoutId+1)
        val peopleNumber = createTextView(
            Color.WHITE,
            currentlySigned,
            10f,
            true,
            width ,
            height / 2,
            startIdentity
        )
        peopleNumberLayout.addView(peopleNumber)


        layout.addView(peopleNumberLayout)

        val lessonLayout = createLayout(false,layoutId+2)
        val lessonNameView = createTextView(
            Color.WHITE,
            lessonName,
            10f,
            false,
            width ,
            height / 2,
            startIdentity+1
        )
        lessonLayout.addView(lessonNameView)
        layout.addView(lessonLayout)

        val revenueLayout = createLayout(false,layoutId+3)
        val revenueView = createTextView(
            Color.WHITE,
            revenue,
            10f,
            true,
            width ,
            height / 2,
            startIdentity+2
        )
        revenueLayout.addView(revenueView)


        layout.addView(revenueLayout)





    }
    // Add table to present the information
    fun addTable(hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,revenue: String) {
        for (i in 4..12) {
            var spaceLayout = createLayout(identitiy = layoutId + i)
            if(i != 4) {
                spaceLayout.addView(blackLineHorz(viewColor = Color.WHITE))
            }
            if(i ==8){
                addLayoutToTable(hour,height,width,layoutId,startIdentity,currentlySigned,lessonName,revenue)
            }
            else{
                spaceLayout.addView(blackLineHorz())
            }
            findViewById<LinearLayout>(R.id.timeLayout).addView(spaceLayout)
        }

    }



    fun removeTables() {
        val primLayout = findViewById<LinearLayout>(R.id.timeLayout)
        var startIdentity = 300000
        var layoutId = 400000
        for (i in 1..24){
            for (j in 0..12) {
                primLayout.removeView(findViewById(layoutId + j))

            }
            for (j in 0..2){
                primLayout.removeView(findViewById(startIdentity + j))
            }
            startIdentity+=10
            layoutId+=10
        }
    }

    // Create a text view according to given parameters
    private fun createTextView(Color: Int = android.graphics.Color.rgb(220,220,220),text:String,size:Float = 10f,isCaps:Boolean = true,width: Float,height: Float,identitiy: Int = -1,toDraw: Boolean = false): TextView{
        val textView = TextView(this)
        textView.setBackgroundColor(Color)
        textView.id = identitiy
        textView.text = text
        textView.textSize = size
        textView.isAllCaps = isCaps
        textView.textSize = 10f
        textView.gravity = Gravity.CENTER
        textView.width = (width/4).toInt()
        textView.height = (height/17).toInt()
        if(toDraw){
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_schedule_24,0,0,0)
        }
        return textView
    }
    // Create basic layout according to given parameters
     fun createLayout(flag: Boolean = true,identitiy:Int = -1,backColor : Int = Color.WHITE) : LinearLayout{
        val linear = LinearLayout(this)
        val id = identitiy
        linear.id = id
        if(flag) {
            linear.orientation = LinearLayout.HORIZONTAL
        }
        else{
            linear.orientation = LinearLayout.VERTICAL
        }
         linear.setBackgroundColor(backColor)
        return linear
    }
    // Creates vertical black line seperator
    private fun blackLineVert() : View{
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            10, LinearLayout.LayoutParams.MATCH_PARENT
        )
        view.setBackgroundColor(Color.BLACK)
        return view
    }
    // Creates horizontal black line seperator
    private fun blackLineHorz(height: Int = 8,viewColor: Int = Color.rgb(60,108,106)) : View{
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height
        )
        view.setBackgroundColor(viewColor)
        return view
    }

    override fun onForward(
        lesson: Lesson,
        date: String,
        startTime: String,
        endTime: String
    ) {
        userId?.let { databl.addLesson(it,"${date}_${startTime}-${endTime}",lesson) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        }
    }

    // Load user
    private fun loadUser(): String? {
        val sharedPref = getSharedPreferences("sharedUser", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }


}