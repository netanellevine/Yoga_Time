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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import Shared.Lesson

class InstructorLessonPopupFragment : DialogFragment() {

    val TAG = "InstructorLesson"
    private var toolbar: Toolbar? = null
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())




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

        val lessonDate = view.findViewById<TextInputEditText>(R.id.lesson_date)
        val currentDate = sdf.format(Date())
        lessonDate.setText(currentDate)

        lessonDate.setOnClickListener {
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = today
            calendar[Calendar.YEAR] = 1950
            val startDate = calendar.timeInMillis

            calendar.timeInMillis = today
            calendar[Calendar.YEAR] = 2003
            val endDate = calendar.timeInMillis

            val constraints: CalendarConstraints = CalendarConstraints.Builder()
                .setOpenAt(endDate)
                .setStart(startDate)
                .setEnd(endDate)
                .build()

            val datePickerBuilder: MaterialDatePicker.Builder<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setTitleText("Select date of birth")
                .setCalendarConstraints(constraints)
            val datePicker = datePickerBuilder.build()
            datePicker.show(childFragmentManager, "datePicker")

            datePicker.addOnPositiveButtonClickListener {
                val date = sdf.format(it)
                lessonDate.setText(date)
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
                    val lessonName = view.findViewById<TextInputEditText>(R.id.lesson_name)
                    val lessonLevel =
                        view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_level)
                    val lessonPrice = view.findViewById<TextInputEditText>(R.id.lesson_price)
                    val lessonParticipants =
                        view.findViewById<TextInputEditText>(R.id.lesson_participants)
                    val lessonDate = view.findViewById<TextInputEditText>(R.id.lesson_date)
                    val lessonStartTime =
                        view.findViewById<TextInputEditText>(R.id.lesson_start_time)
                    val lessonEndTime = view.findViewById<TextInputEditText>(R.id.lesson_end_time)
                    val lessonDescription =
                        view.findViewById<TextInputEditText>(R.id.lesson_description)

                    val lesson = Lesson(
                            lessonName.text.toString(),
                            lessonParticipants.text.toString().toInt(),
                            lessonLevel.text.toString(),
                            lessonPrice.text.toString().toDouble(),
                            lessonDescription.text.toString()
                    )


                    mOnForwardListener.onForward(lesson,lessonDate.text.toString(),lessonStartTime.text.toString(),lessonEndTime.text.toString())
                    dismiss()
                }
            }

            dismiss()
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