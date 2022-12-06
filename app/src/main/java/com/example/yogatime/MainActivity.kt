package com.example.yogatime

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.yogatime.databinding.ActivityMainBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnboardingItems()
        supportActionBar?.hide()
    }

    private fun setOnboardingItems() {
//        onboardingItemsAdapter = OnboardingItemsAdapter(
//            listOf(
//                OnboardingItem(
//                    onboardingImage = R.drawable.ic_launcher_foreground,
//                    title = "Find your perfect yoga pose",
//                    description = "We have a wide range of yoga poses for you to choose from"
//                ),
//                OnboardingItem(
//                    onboardingImage = R.drawable.ic_launcher_foreground,
//                    title = "Find your perfect yoga pose",
//                    description = "We have a wide range of yoga poses for you to choose from"
//                ),
//                OnboardingItem(
//                    onboardingImage = R.drawable.ic_launcher_foreground,
//                    title = "Find your perfect yoga pose",
//                    description = "We have a wide range of yoga poses for you to choose from"
//                )
//            )
//        )
//
//        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
//        onboardingViewPager.adapter = onboardingItemsAdapter
//        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
//        dotsIndicator.attachTo(onboardingViewPager)
    }
}
