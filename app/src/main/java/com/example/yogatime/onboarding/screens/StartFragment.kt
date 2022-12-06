package com.example.yogatime.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yogatime.R


class StartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        val viewPager =
            activity?.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)

        val btn_next =
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_next)
        btn_next.setOnClickListener {
            viewPager?.currentItem = 1

        }

        return view
    }

}