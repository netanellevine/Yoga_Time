package com.example.yogatime

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import businessLogic.DataBL


class InstructorLessonPopupFragment(data: DataBL,user: String) : DialogFragment() {

    val TAG = "InstructorLessonPopupFragment"
    private var toolbar: Toolbar? = null

    init {
        val dataBL = data
        val userId = user
    }


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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar!!.setNavigationOnClickListener { v: View? -> dismiss() }
        toolbar!!.title = "Create Lesson"
        toolbar!!.inflateMenu(R.menu.instructor_lesson_popup)
        toolbar!!.setOnMenuItemClickListener { item: MenuItem? ->
            if (item != null) {
                if(item.itemId == R.id.action_save ){
                                Toast.makeText(view.context, "Nothing Selected",
                Toast.LENGTH_SHORT).show()
            Log.d(tag,"Hello from fragment")
                }
                    dismiss()
            }
            true
        }
//        val saveButton = toolbar.findViewById<Button>(R.id.action_save)
//        saveButton.setOnClickListener {
//            Toast.makeText(view.context, "Nothing Selected",
//                Toast.LENGTH_SHORT).show()
//            Log.d(tag,"Hello from fragment")
//        }
    }

    companion object {
        fun display(fragmentManager: FragmentManager?,data: DataBL,user: String): InstructorLessonPopupFragment? {
            val exampleDialog = InstructorLessonPopupFragment(data,user)
            if (fragmentManager != null) {
                exampleDialog.show(fragmentManager, exampleDialog.TAG)
            }
            return exampleDialog
        }
    }


}