package com.example.yogatime.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.yogatime.R

class LoadingDialog(act: Activity) {
    private var activity: Activity
    private lateinit var dialog: AlertDialog

    init {
        activity = act
    }

    fun startLoadingDialog(){
        val builder = AlertDialog.Builder(activity)
        val inflater:LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_screen,null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

}