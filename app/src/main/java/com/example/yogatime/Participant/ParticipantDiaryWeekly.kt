package com.example.yogatime.Participant


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import businessLogic.DataBL
import com.example.yogatime.R
import com.example.yogatime.utils.LoadingDialog
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class ParticipantDiaryWeekly: AppCompatActivity() {
    var userId: String? = null
    @RequiresApi(Build.VERSION_CODES.N)
    var databl = DataBL()
    var width by Delegates.notNull<Float>()
    var height by Delegates.notNull<Float>()
    lateinit var loading:LoadingDialog



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize variables

        width = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.participant_weekly)
        userId = loadUser()
        if(userId!= null) {
            userId = this.intent?.getSerializableExtra("userId") as String
        }
        loading = LoadingDialog(this)

        // Initialize calender view
        val weekCalendarView: WeekCalendarView = findViewById(R.id.weekCalendarView)
        val red = Color.rgb(14,75,84)
        val white = Color.WHITE
        var markedContainer: DayViewContainer? = null




        //Initialize the view
        weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            @SuppressLint("SetTextI18n")
            override fun bind(container: DayViewContainer, data: WeekDay) {
                // Build the daily view
                val formatter = DateTimeFormatter.ofPattern("MM-dd")
                val yearFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                container.textView.text = data.date.dayOfWeek.toString().substring(0,3) + '\n' + data.date.format(formatter)
                container.textView.textSize = width/100
                container.textView.setTextColor(red)
                container.view.setBackgroundColor(white)
                container.view.setOnClickListener {
                    if (markedContainer != container) {
                        removeTables()
                        changeColor(container, red, white)
                        changeColor(markedContainer, white, red)

                        val year = data.date.format(yearFormat)

                        userId?.let { it1 -> databl.getAvailability(it1,year,::addTable) }


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
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.N)
    fun addLayoutToTable(hour:String, height: Float, width: Float, layoutId: Int, startIdentity: Int, currentlySigned: String, lessonName:String, level:String, price:String, addButton:Boolean, buttonFunc: (flag:Boolean) -> Unit, year:String) {
        val hourView =
            createTextView(text = hour, height = height, width = width, toDraw = true, size = 13f)

        val layout = createLayout(identitiy = layoutId + 10)
        findViewById<LinearLayout>(R.id.timeLayout).addView(layout)

        layout.addView(hourView)
        layout.addView(LineVert())

        val peopleNumberLayout = createLayout(false, layoutId + 1)
        val peopleNumber = createTextView(
            Color.WHITE,
            currentlySigned,
            11f,
            true,
            width / 2,
            height,
            startIdentity
        )
        peopleNumberLayout.addView(peopleNumber)

        layout.addView(peopleNumberLayout)
//        layout.addView(LineVert())

        val lessonLayout = createLayout(false, layoutId + 2)
        val lessonNameView = createTextView(
            Color.WHITE,
            lessonName,
            11f,
            false,
            width,
            height,
            startIdentity + 1
        )
        lessonLayout.addView(lessonNameView)
        layout.addView(lessonLayout)
//        layout.addView(LineVert())

        val currColor: Int = if (level == "A") Color.GREEN
        else if (level == "B") Color.YELLOW
        else if (level == "C") Color.RED
        else Color.BLUE
        val levelLayout = createLayout(false, layoutId + 2)
        val levelNameView = createTextView(
            Color.WHITE,
            level,
            12f,
            false,
            width / 2,
            height,
            startIdentity + 2,
            textColor = currColor
        )
        levelLayout.addView(levelNameView)
        layout.addView(levelLayout)
//        layout.addView(LineVert())

        val priceLayout = createLayout(false, layoutId + 3)
        val priceView = createTextView(
            Color.WHITE,
            price,
            11f,
            true,
            width / 2,
            height,
            startIdentity + 3
        )
        priceLayout.addView(priceView)



        layout.addView(priceLayout)


        val signButton = ImageButton(this)
        val signLayout = createLayout(false, layoutId + 4)
        val cur = !addButton
        val sign = if (cur)  getDrawable(R.drawable.ic_plus_24) else getDrawable(R.drawable.ic_baseline_remove_24)
        signButton.setImageDrawable(sign)
        signButton.setOnClickListener {
            loading.startLoadingDialog()
            val scope = CoroutineScope(newSingleThreadContext("Assign user"))
            removeTables()

            scope.launch {
                    buttonFunc(cur)
                runOnUiThread {
                    userId?.let { it1 -> databl.getAvailability(it1, year, ::addTable) }
                }
                    TimeUnit.SECONDS.sleep(2L)
            loading.dismissDialog()

            }

        }
        signLayout.addView(signButton)
        layout.addView(signLayout)
//        if (!addButton){
//            val plusButton = ImageButton(this)
//            val plusLayout = createLayout(false, layoutId + 4)
//            plusButton.setImageDrawable(getDrawable(R.drawable.ic_plus_24))
//            plusButton.setOnClickListener {
//                buttonFunc(true)
//                layout.removeView(plusLayout)
//            }
//            plusLayout.addView(plusButton)
//            layout.addView(plusLayout)
//        }
//        else{
//            val removeButton = ImageButton(this)
//            val removeLayout = createLayout(false, layoutId + 4)
//
//            removeButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24))
//            removeButton.setOnClickListener {
//                buttonFunc(false)
//                layout.removeView(removeLayout)
//            }
//            removeLayout.addView(removeButton)
//            layout.addView(removeLayout)
//        }
//        layout.addView(LineVert())

    }

    // Add table to present the information
    @RequiresApi(Build.VERSION_CODES.N)
    fun addTable(hour:String, startIdentity:Int, layoutId:Int, currentlySigned: String, lessonName: String, level:String, price: String, addButton: Boolean, buttonFunc: (flag:Boolean) -> Unit, year: String) {

        addLayoutToTable(hour,height,width,layoutId,startIdentity,currentlySigned,lessonName,level,price,addButton,buttonFunc,year)
        val spaceLayout = createLayout(identitiy = layoutId)
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
    private fun createTextView(BgColor: Int = Color.rgb(220,220,220),text:String,size:Float = 10f,isCaps:Boolean = true,width: Float,height: Float,identitiy: Int = -1,toDraw: Boolean = false, textColor: Int = -1): TextView{
        val textView = TextView(this)
        textView.setBackgroundColor(BgColor)
        textView.id = identitiy
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
    private fun LineVert(width: Int = 6, viewColor: Int = Color.rgb(60,108,106)) : View{
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            width, LinearLayout.LayoutParams.MATCH_PARENT
        )
        view.setBackgroundColor(viewColor)
        return view
    }
    // Creates horizontal black line seperator
    private fun LineHorz(height: Int = 6, viewColor: Int = Color.rgb(60,108,106)) : View{
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height
        )
        view.setBackgroundColor(viewColor)
        return view
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

        val lessonLayout = createLayout(false)
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

        val levelLayout = createLayout(false)
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

        val priceLayout = createLayout(false)
        val priceView = createTextView(
            Color.rgb(220,220,220),
            "Price",
            11f,
            false,
            width / 2 ,
            height,
            textColor = Color.rgb(21, 115, 135)
        )
        priceLayout.addView(priceView)
        layout.addView(priceLayout)
//        layout.addView(LineVert())

        val lineLayoutBottom = createLayout()
        findViewById<LinearLayout>(R.id.timeLayout).addView(lineLayoutBottom)
        lineLayoutBottom.addView(LineHorz())
    }


}