package com.example.yogatime.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yogatime.R
import com.example.yogatime.onboarding.screens.PhoneScreen
import com.example.yogatime.onboarding.screens.StartFragment

class ViewPagerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            StartFragment(),
            PhoneScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter

        return view
    }
}