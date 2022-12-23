package com.example.yogatime

import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorSpace.Rgb
import android.os.Build

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi


import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import businessLogic.DataBL
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.kizitonwose.calendar.core.*

import com.kizitonwose.calendar.view.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import java.util.*


class InstructorDiaryWeekly: AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize variables
        val width = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instructor_weekly)
        val userId: String? = intent.getSerializableExtra("userId",String::class.java)
        // Initialize calender view
        val weekCalendarView: WeekCalendarView = findViewById(R.id.weekCalendarView)
        val red = Color.rgb(14,75,84)
        val white = Color.WHITE
        var markedContainer: DayViewContainer? = null
        var databl = DataBL()

        // Add save popup
        val saveDatePopup = findViewById<FloatingActionButton>(R.id.floating_action_button)

        saveDatePopup.setOnClickListener{
            val instructorLessonPopupFragment = InstructorLessonPopupFragment
            if (userId != null) {
                instructorLessonPopupFragment.display(supportFragmentManager,databl,userId)
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
                container.textView.text = data.date.dayOfWeek.toString().substring(0,3) + '\n' +"${data.date.format(formatter)}"
                container.textView.textSize = width/100
                container.textView.setTextColor(red)
                container.view.setBackgroundColor(white)
                container.view.setOnClickListener(object : DoubleClickListener() {
                    override fun onDoubleClick(v: View) {
                        changeColor(container,red,white)
                        changeColor(markedContainer,white,red)
                        markedContainer = container
//                        removeTables()
//                        addTables(
//                            data.date.dayOfWeek.toString().substring(0,3) + '\n' +
//                                data.date.month.toString().substring(0,3) + " ${data.date.dayOfMonth}" ,width,height)
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
    fun addLayoutToTable(hour:String,height: Float,width: Float,layoutId: Int,startIdentity: Int){
        val hourView = createTextView(text=hour, height = height/2, width = width-400, toDraw = true)

        val layout = createLayout(identitiy = layoutId)
        findViewById<LinearLayout>(R.id.timeLayout).addView(layout)




        layout.addView(hourView)

            val peopleNumberLayout = createLayout(false,15150)
            val peopleNumber = createTextView(
                Color.WHITE,
                "  Number of people  ",
                10f,
                true,
                width * 2,
                height / 2,
                startIdentity
            )
            peopleNumberLayout.addView(peopleNumber)


            layout.addView(peopleNumberLayout)


    }
    // Add table to present the information
    fun addTable(hour:String,width:Float,height:Float,startIdentity:Int,layoutId:Int) {
        for (i in 2..10) {
            var spaceLayout = createLayout(identitiy = layoutId + i)
            if(i != 2) {
                spaceLayout.addView(blackLineHorz(viewColor = Color.WHITE))
            }
            if(i ==6){
                addLayoutToTable(hour,height,width,layoutId,startIdentity)
            }
            else{
                spaceLayout.addView(blackLineHorz())
            }
            findViewById<LinearLayout>(R.id.timeLayout).addView(spaceLayout)
        }

    }

    fun addTables(date: String,width: Float,height: Float){
        for (i in 8..19) {
                addTable(date, width,height, startIdentity = i*10000, layoutId = i*100000)
        }

    }

    fun removeTables(){
        val primLayout = findViewById<LinearLayout>(R.id.timeLayout)
        for (i in 8..19) {
            val id = i*100000
            primLayout.removeView(findViewById(id))
            primLayout.removeView(findViewById(id+1))
            primLayout.removeView(findViewById(id+2))
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

}