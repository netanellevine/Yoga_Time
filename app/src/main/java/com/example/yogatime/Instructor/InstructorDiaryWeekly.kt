package com.example.yogatime.Instructor

import Shared.Lesson
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi


import androidx.appcompat.app.AppCompatActivity
import businessLogic.DataBL
import com.example.yogatime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.kizitonwose.calendar.core.*

import com.kizitonwose.calendar.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import kotlin.properties.Delegates


class InstructorDiaryWeekly: AppCompatActivity(), InstructorLessonPopupFragment.OnForwardListener {
    var userId: String? = null
    @RequiresApi(Build.VERSION_CODES.N)
    var databl = DataBL()
    var width by Delegates.notNull<Float>()
    var height by Delegates.notNull<Float>()
    val GETCOLOR:  (Int) -> Int = {color: Int -> resources.getColor(color)}

    private lateinit var bottomNavigationView: BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize variables
        width = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instructor_weekly)
        userId = loadUser()
        if(userId!= null) {
            val user = this.intent?.getSerializableExtra("userId")
            if (user != null) {
                userId = user as String
            }
            if (userId != null) {
                Log.d("getUserId", userId!!)
            }
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
                InstructorLessonPopupFragment.display(supportFragmentManager)
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
                container.view.setOnClickListener {

                    if (markedContainer != container) {
                        removeTables()
                        changeColor(container, red, white)
                        changeColor(markedContainer, white, red)

                        val year = data.date.format(yearFormat)
                        userId?.let {
                            databl.getInstructorTimeFromDatabase(
                                it,
                                year,
                                ::addTable
                            )
                        }

                        markedContainer = container
                    }

                    }

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
        addHeaderToTable()


        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.schedule_instructor_nav
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.personal_profile_instructor_nav -> {
                    startActivity(Intent(this, InstructorProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.schedule_instructor_nav -> {
                    true
                }
                else -> false
            }
        }

    }



    // Container view add the text to the container
    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
        // With ViewBinding
        // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    }
    // Switch color of a container
    fun changeColor(container: DayViewContainer?, color1:Int, color2:Int){
        container?.view?.setBackgroundColor(color1)
        container?.textView?.setTextColor(color2)

    }
    // Add layout to the table, which we use to present the lesson information
    fun addLayoutToTable(hour:String,height: Float,width: Float,layoutId: Int,startIdentity: Int,currentlySigned: String,lessonName:String,level:String,revenue:String){
        val hourView = createTextView(text=hour, height = height, width = width, toDraw = true, size=13f)

        val layout = createLayout(identitiy = layoutId + 10)
        findViewById<LinearLayout>(R.id.timeLayout).addView(layout)

        layout.addView(hourView)
        layout.addView(LineVert())

        val peopleNumberLayout = createLayout(false,layoutId+1)
        val peopleNumber = createTextView(
            Color.WHITE,
            currentlySigned,
            11f,
            true,
            width  / 2,
            height,
            startIdentity
        )
        peopleNumberLayout.addView(peopleNumber)

        layout.addView(peopleNumberLayout)
//        layout.addView(LineVert())

        val lessonLayout = createLayout(false,layoutId+2)
        val lessonNameView = createTextView(
            Color.WHITE,
            lessonName,
            11f,
            false,
            width ,
            height,
            startIdentity+1
        )
        lessonLayout.addView(lessonNameView)
        layout.addView(lessonLayout)
//        layout.addView(LineVert())

        val levelLayout = createLayout(false,layoutId+2)
        var currColor = 0
        if (level == "A") currColor = R.color.EASY
        else if (level == "B") currColor = R.color.MODERATE
        else if (level == "C") currColor = R.color.ADVANCED
        else currColor = R.color.ALL
        val levelNameView = createTextView(
            Color.WHITE,
            level,
            12f,
            false,
            width / 2 ,
            height,
            startIdentity+2,
            textColor = GETCOLOR(currColor)
        )
        levelLayout.addView(levelNameView)
        layout.addView(levelLayout)
//        layout.addView(LineVert())

        val revenueLayout = createLayout(false,layoutId+3)
        val revenueView = createTextView(
            Color.WHITE,
            revenue,
            11f,
            true,
            width / 2 ,
            height,
            startIdentity+3
        )
        revenueLayout.addView(revenueView)
        layout.addView(revenueLayout)
//        layout.addView(LineVert())

    }

    // Add table to present the information
    fun addTable(hour:String,startIdentity:Int,layoutId:Int,currentlySigned: String,lessonName: String,level:String,revenue: String) {
//        for (i in 4..12) {
//            var spaceLayout = createLayout(identitiy = layoutId + i)
//            if(i != 4) {
//                spaceLayout.addView(blackLineHorz(viewColor = Color.WHITE))
//            }
//            if(i ==8){
//                addLayoutToTable(hour,height,width,layoutId,startIdentity,currentlySigned,lessonName,level,revenue)
//            }
//            else{
//                spaceLayout.addView(blackLineHorz())
//            }
//            findViewById<LinearLayout>(R.id.timeLayout).addView(spaceLayout)
//        }
        addLayoutToTable(hour,height,width,layoutId,startIdentity,currentlySigned,lessonName,level,revenue)
        var spaceLayout = createLayout(identitiy = layoutId)
        spaceLayout.addView(LineHorz())
        findViewById<LinearLayout>(R.id.timeLayout).addView(spaceLayout)

    }



    fun removeTables() {
        val primLayout = findViewById<LinearLayout>(R.id.timeLayout)
        var startIdentity = 300000
        var layoutId = 400000
        for (i in 1..24){
            primLayout.removeView(findViewById(layoutId + 10))
            primLayout.removeView(findViewById(layoutId))
//            for (j in 0..12) {
//                primLayout.removeView(findViewById(layoutId + j))
//
//            }
//            for (j in 0..3){
//                primLayout.removeView(findViewById(startIdentity + j))
//            }
            startIdentity+=100
            layoutId+=100
        }
    }

    // Create a text view according to given parameters
    private fun createTextView(BgColor: Int = GETCOLOR(R.color.WeeklyBackgroundColor), text:String, size:Float = 10f, isCaps:Boolean = true, width: Float, height: Float, identity: Int = -1, toDraw: Boolean = false, textColor: Int = -1): TextView{
        val textView = TextView(this)
        textView.setBackgroundColor(BgColor)
        textView.id = identity
        textView.text = text
        textView.textSize = size
        textView.isAllCaps = isCaps
        textView.gravity = Gravity.CENTER
        textView.width = (width/4).toInt()
        textView.height = (height/17).toInt()
        if (textColor != -1) {
            textView.setTextColor(textColor)
        }
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
    private fun LineVert(width: Int = 6, viewColor: Int = resources.getColor(R.color.TextColor)) : View{
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            width, LinearLayout.LayoutParams.MATCH_PARENT
        )
        view.setBackgroundColor(viewColor)
        return view
    }
    // Creates horizontal black line seperator
    private fun LineHorz(height: Int = 6, viewColor: Int = resources.getColor(R.color.TextColor)) : View{
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height
        )
        view.setBackgroundColor(viewColor)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onForward(
        lesson: Lesson,
        date: String,
        startTime: String,
        endTime: String
    ) {
        val act = this

            userId?.let {
                databl.addLesson(it, "${date}_${startTime}-${endTime}", lesson) { message ->
                    Toast.makeText(act, message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Load user
    private fun loadUser(): String? {
        val sharedPref = getSharedPreferences("sharedUser", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }

    // Add layout to the table, which we use to present the lesson information
    fun addHeaderToTable(){
        val lineLayoutTop = createLayout()
        findViewById<LinearLayout>(R.id.timeLayout).addView(lineLayoutTop)
        lineLayoutTop.addView(LineHorz(12))
        val hourView = createTextView(text="Time", height = height, width = width, size=11f, isCaps=false, textColor = Color.rgb(21, 115, 135))

        val layout = createLayout()
        findViewById<LinearLayout>(R.id.timeLayout).addView(layout)
//        layout.orientation = LinearLayout.VERTICAL
//        layout.addView(LineHorz(12))
//        layout.orientation = LinearLayout.HORIZONTAL
        layout.addView(hourView)
        layout.setBackgroundColor(Color.rgb(220,220,220))
//        layout.addView(LineVert())

        val peopleNumberLayout = createLayout(false)
        val peopleNumber = createTextView(
            Color.rgb(220,220,220),
            "Availability/\nCapacity",
            10f,
            false,
            width / 2,
            height,
            textColor = Color.rgb(21, 115, 135)
        )
        peopleNumberLayout.addView(peopleNumber)

        layout.addView(peopleNumberLayout)
//        layout.addView(LineVert())

        val lessonLayout = createLayout(false,)
        val lessonNameView = createTextView(
            Color.rgb(220,220,220),
            "Lesson",
            11f,
            false,
            width ,
            height,
            textColor = Color.rgb(21, 115, 135)
        )
        lessonLayout.addView(lessonNameView)
        layout.addView(lessonLayout)
//        layout.addView(LineVert())

        val levelLayout = createLayout(false,)
        val levelNameView = createTextView(
            Color.rgb(220,220,220),
            "Level",
            11f,
            false,
            width / 2 ,
            height,
            textColor = Color.rgb(21, 115, 135)
        )
        levelLayout.addView(levelNameView)
        layout.addView(levelLayout)
//        layout.addView(LineVert())

        val revenueLayout = createLayout(false)
        val revenueView = createTextView(
            Color.rgb(220,220,220),
            "Revenue",
            11f,
            false,
            width / 2 ,
            height,
            textColor = Color.rgb(21, 115, 135)
        )
        revenueLayout.addView(revenueView)
        layout.addView(revenueLayout)
//        layout.addView(LineVert())

        val lineLayoutBottom = createLayout()
        findViewById<LinearLayout>(R.id.timeLayout).addView(lineLayoutBottom)
        lineLayoutBottom.addView(LineHorz())
    }


}