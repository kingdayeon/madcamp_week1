package com.example.myapplication_test1.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication_test1.ui.home.HomeFragment
import com.example.myapplication_test1.ui.dashboard.DashboardFragment
import com.example.myapplication_test1.ui.appointments.AppointmentsFragment

class swipe_adapter(
    fragmentActivity: FragmentActivity,
    private val navController: NavController
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> DashboardFragment()
            2 -> AppointmentsFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
