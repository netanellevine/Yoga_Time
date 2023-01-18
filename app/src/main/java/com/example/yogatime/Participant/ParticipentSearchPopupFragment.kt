package com.example.yogatime.Participant

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

import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

import Shared.participantFilter
import com.example.yogatime.R


class ParticipentSearchPopupFragment : DialogFragment() {

    val TAG = "searchLesson"
    private var toolbar: Toolbar? = null




    interface OnForwardListener {
        fun onForward(filter: participantFilter)
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
            inflater.inflate(R.layout.fragment_participant_search_popup, container, false)
        toolbar = view.findViewById(R.id.toolbar)

        val lessonLevel = view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_level)
        val items = resources.getStringArray(R.array.lesson_filter)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        lessonLevel.setText(items[0], false)
        lessonLevel.setAdapter(adapter)

        val lessonName = view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_name)
        val items2 = resources.getStringArray(R.array.lesson_names)
        items2.sort()
        val adapter2 = ArrayAdapter(requireContext(), R.layout.list_item, items2)
        lessonName.threshold = 1
        lessonName.setAdapter(adapter2)












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
        toolbar!!.title = "Search Lesson"
        toolbar!!.inflateMenu(R.menu.participant_search_popup)
        toolbar!!.setOnMenuItemClickListener { item: MenuItem? ->
            if (item != null) {
                if (item.itemId == R.id.action_search) {
                    val instructorName = view.findViewById<TextInputEditText>(R.id.instructor_name)
                    val lessonName = view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_name)
                    val lessonLevel = view.findViewById<MaterialAutoCompleteTextView>(R.id.lesson_level)
                    val lessonPrice = view.findViewById<TextInputEditText>(R.id.lesson_price)
                    var Iname = instructorName.text.toString()
                    var Lname = lessonName.text.toString()
                    var Llevel = lessonLevel.text.toString()
                    var Lprice = lessonPrice.text.toString()
                    var Dprice = 0.0
                    if (Iname.isEmpty()){
                        Iname = "any"
                    }
                    if (Lname.isEmpty()){
                        Lname = "any"
                    }
                    if (Llevel.isEmpty()){
                        Llevel = "any"
                    }
                    if (Lprice.isNotEmpty()){
                        Dprice = Lprice.toDouble()
                    }

                    val searchFilter = participantFilter(Iname,
                                                            Lname,
                                                            listOf(Llevel),
                                                            Dprice
                                                        )


                        mOnForwardListener.onForward(searchFilter)
                        dismiss()

                }
            } else {
                dismiss()
            }

            true
        }
    }

    companion object {
        fun display(fragmentManager: FragmentManager?): ParticipentSearchPopupFragment? {
            val exampleDialog = ParticipentSearchPopupFragment()
            if (fragmentManager != null) {
                exampleDialog.show(fragmentManager, exampleDialog.TAG)
            }
            return exampleDialog
        }
    }


}