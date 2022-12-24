package com.example.yogatime

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import Shared.Lesson


class InstructorLessonPopupFragment : DialogFragment() {

    val TAG = "InstructorLesson"
    private var toolbar: Toolbar? = null
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val sdf2 = SimpleDateFormat("HH:mm", Locale.getDefault())



    interface OnForwardListener {
        fun onForward(lesson: Lesson,date:String,startTime:String,endTime:String)
    }

    lateinit var mOnForwardListener: OnForwardListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)

    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.AppTheme_Slide)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View =
            inflater.inflate(R.layout.fragment_instructor_lesson_popup, container, false)
        toolbar = view.findViewById(R.id.toolbar)

        val lessonLevel = view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_level)
        val items = resources.getStringArray(R.array.lesson_levels)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        lessonLevel.setText(items[0], false)
        lessonLevel.setAdapter(adapter)

        val lessonName = view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_name)
        val items2 = resources.getStringArray(R.array.lesson_names)
        items2.sort()
        val adapter2 = ArrayAdapter(requireContext(), R.layout.list_item, items2)
        lessonName.threshold = 1
        lessonName.setAdapter(adapter2)

//        var today = MaterialDatePicker.todayInUtcMilliseconds()
        var today = System.currentTimeMillis()
        var calendar = Calendar.getInstance()
        calendar.timeInMillis = today

        val lessonDateLayout = view.findViewById<TextInputLayout>(R.id.lesson_date_layout)
        val lessonDate = view.findViewById<TextInputEditText>(R.id.lesson_date)
        // if theres less than 1 hour left in the day, set the date to tomorrow
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 23) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            today = calendar.timeInMillis
        }
        val currentDate = sdf.format(Date())
        lessonDate.setText(currentDate)

        lessonDate.setOnClickListener {
            today = MaterialDatePicker.todayInUtcMilliseconds()
            calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = today
            // if theres less than 1 hour left in the day, set the date to tomorrow
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 23) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                today = calendar.timeInMillis
            }
            val startDate = calendar.timeInMillis

            calendar.timeInMillis = today
            calendar[Calendar.YEAR] = 2050
            calendar[Calendar.MONTH] = Calendar.DECEMBER
            calendar[Calendar.DAY_OF_MONTH] = 31
            val endDate = calendar.timeInMillis

            val constraints: CalendarConstraints = CalendarConstraints.Builder()
                .setOpenAt(startDate)
                .setStart(startDate)
                .setEnd(endDate)
                .setValidator(DateValidatorPointForward.now())
                .build()

            val datePickerBuilder: MaterialDatePicker.Builder<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(today)
                .setTitleText("Select the date of the lesson")
                .setCalendarConstraints(constraints)
            val datePicker = datePickerBuilder.build()
            datePicker.show(childFragmentManager, "datePicker")

            datePicker.addOnPositiveButtonClickListener {
                // validate selected date is not in the past
                val selectedDate = Date(it)
                val currDate = Date()
                if (selectedDate.before(currDate)) {
                    lessonDateLayout.error = "Date cannot be in the past"
                    lessonDate.setText("")
                } else {
                    lessonDateLayout.error = null
                    lessonDate.setText(sdf.format(selectedDate))
                }
            }
        }

//        today = MaterialDatePicker.todayInUtcMilliseconds()
        today = System.currentTimeMillis()
        calendar = Calendar.getInstance()
        calendar.timeInMillis = today

        val lessonStartTimeLayout =
            view.findViewById<TextInputLayout>(R.id.lesson_start_time_layout)
        val lessonStartTime = view.findViewById<TextInputEditText>(R.id.lesson_start_time)
        // Set the start time to the nearest hour. because the date picker selects default today unless there is at least 1 hour left in the day we can assume that the start time is at least 1 hour from now
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        calendar[Calendar.MINUTE] = 0
        val startTime = calendar.timeInMillis
        lessonStartTime.setText(sdf2.format(startTime))

        val lessonEndTimeLayout = view.findViewById<TextInputLayout>(R.id.lesson_end_time_layout)
        val lessonEndTime = view.findViewById<TextInputEditText>(R.id.lesson_end_time)
        // Set the end time to the nearest hour. because the date picker selects default today unless there is at least 1 hour left in the day we can assume that the end time is at least 2 hours from now. cap the end time at 23:59
//        if (calendar.get(Calendar.HOUR_OF_DAY) < 23) {
//            calendar.add(Calendar.HOUR_OF_DAY, 1)
//        } else {
//        }
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        val endTime = calendar.timeInMillis
        lessonEndTime.setText(sdf2.format(endTime))



        lessonStartTime.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(sdf2.format(Date()).split(":")[0].toInt())
                .setMinute(sdf2.format(Date()).split(":")[1].toInt())
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()
            timePicker.show(childFragmentManager, "timePicker")

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                val time =
                    hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')
                val startTime = (hour.toString().padStart(2, '0') + minute.toString().padStart(2, '0')).toInt()

                val endTime = lessonEndTime.text.toString()
                if (endTime.isNotEmpty()) {
                    val endTimeInt =
                        endTime.split(":")[0].toInt() * 100 + endTime.split(":")[1].toInt()
                    if (startTime >= endTimeInt) {
                        lessonStartTimeLayout.error = "Start time must be before end time"
                        calendar = Calendar.getInstance()
                        calendar.timeInMillis = System.currentTimeMillis()
                        calendar.add(Calendar.HOUR_OF_DAY, 1)
                        calendar[Calendar.MINUTE] = 0
                        lessonStartTime.setText(sdf2.format(calendar.timeInMillis))
//                        lessonStartTime.setText("")
                    } else {
                        lessonStartTimeLayout.error = null
                        lessonEndTimeLayout.error = null
                        lessonStartTime.setText(time)
                    }
                }

            }
        }

        lessonEndTime.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()
            timePicker.show(childFragmentManager, "timePicker")

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                val time =
                    hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')
                val endTime = (hour.toString().padStart(2, '0') + minute.toString().padStart(2, '0')).toInt()

                val startTime = lessonStartTime.text.toString()
                if (startTime.isNotEmpty()) {
                    val startTimeInt =
                        startTime.split(":")[0].toInt() * 100 + startTime.split(":")[1].toInt()
                    if (startTimeInt >= endTime) {
                        lessonEndTimeLayout.error = "End time must be after start time"
//                        lessonEndTime.setText("")
                        lessonEndTime.setText("23:59")
                    } else {
                        lessonEndTimeLayout.error = null
                        lessonStartTimeLayout.error = null
                        lessonEndTime.setText(time)
                    }
                }

            }
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            mOnForwardListener = activity as OnForwardListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() + " must implement OnForwardListener")
            )
        }

        toolbar!!.setNavigationOnClickListener { v: View? -> dismiss() }
        toolbar!!.title = "Create Lesson"
        toolbar!!.inflateMenu(R.menu.instructor_lesson_popup)
        toolbar!!.setOnMenuItemClickListener { item: MenuItem? ->
            if (item != null) {
                if (item.itemId == R.id.action_save) {
                    val lessonName = view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_name)
                    val lessonNameLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_name_layout)
                    val lessonLevel =
                        view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_level)
                    val lessonLevelLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_level_layout)
                    val lessonPrice = view.findViewById<TextInputEditText>(R.id.lesson_price)
                    val lessonPriceLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_price_layout)
                    val lessonParticipants =
                        view.findViewById<TextInputEditText>(R.id.lesson_participants)
                    val lessonParticipantsLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_participants_layout)
                    val lessonDate = view.findViewById<TextInputEditText>(R.id.lesson_date)
                    val lessonDateLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_date_layout)
                    val lessonStartTime =
                        view.findViewById<TextInputEditText>(R.id.lesson_start_time)
                    val lessonStartTimeLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_start_time_layout)
                    val lessonEndTime = view.findViewById<TextInputEditText>(R.id.lesson_end_time)
                    val lessonEndTimeLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_end_time_layout)
                    val lessonDescription =
                        view.findViewById<TextInputEditText>(R.id.lesson_description)
                    val lessonDescriptionLayout =
                        view.findViewById<TextInputLayout>(R.id.lesson_description_layout)

                    var valid = true

                    if (lessonName.text.toString().isEmpty()) {
                        lessonNameLayout.error = "Lesson name cannot be empty"
                        valid = false
                    } else {
                        lessonNameLayout.error = null
                    }
                    if (lessonLevel.text.toString().isEmpty()) {
                        lessonLevelLayout.error = "Lesson level cannot be empty"
                        valid = false
                    } else {
                        lessonLevelLayout.error = null
                    }
                    if (lessonPrice.text.toString().isEmpty()) {
                        lessonPriceLayout.error = "Lesson price cannot be empty"
                        valid = false
                    } else {
                        lessonPriceLayout.error = null
                    }
                    if (lessonParticipants.text.toString().isEmpty()) {
                        lessonParticipantsLayout.error = "Lesson participants cannot be empty"
                        valid = false
                    } else {
                        lessonParticipantsLayout.error = null
                    }
                    if (lessonDate.text.toString().isEmpty()) {
                        lessonDateLayout.error = "Lesson date cannot be empty"
                        valid = false
                    } else {
                        lessonDateLayout.error = null
                    }
                    if (lessonStartTime.text.toString().isEmpty()) {
                        lessonStartTimeLayout.error = "Lesson start time cannot be empty"
                        valid = false
                    } else {
                        lessonStartTimeLayout.error = null
                    }
                    if (lessonEndTime.text.toString().isEmpty()) {
                        lessonEndTimeLayout.error = "Lesson end time cannot be empty"
                        valid = false
                    } else {
                        lessonEndTimeLayout.error = null
                    }

                    if (valid) {
                        val lesson = Lesson(
                            lessonName.text.toString(),
                            lessonParticipants.text.toString().toInt(),
                            lessonLevel.text.toString(),
                            lessonPrice.text.toString().toDouble(),
                            lessonDescription.text.toString()
                        )
                        mOnForwardListener.onForward(lesson,lessonDate.text.toString(),
                            lessonStartTime.text.toString(),
                            lessonEndTime.text.toString())
                        dismiss()
                    }
                }
            } else {
                dismiss()
            }

            true
        }
    }

    companion object {
        fun display(fragmentManager: FragmentManager?): InstructorLessonPopupFragment? {
            val exampleDialog = InstructorLessonPopupFragment()
            if (fragmentManager != null) {
                exampleDialog.show(fragmentManager, exampleDialog.TAG)
            }
            return exampleDialog
        }
    }


}