package com.example.myapplication_test1.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication_test1.ui.home.HomeFragment
import com.example.myapplication_test1.ui.dashboard.DashboardFragment
import com.example.myapplication_test1.ui.appointments.AppointmentsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // 총 페이지 수 반환
    override fun getItemCount(): Int = 3

    // position에 따른 Fragment 반환
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()         // 첫 번째 탭
            1 -> DashboardFragment()    // 두 번째 탭
            2 -> AppointmentsFragment() // 세 번째 탭
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}