package com.example.myapplication_test1

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication_test1.data.Friend
import com.example.myapplication_test1.databinding.ActivityMainBinding
import com.example.myapplication_test1.ui.home.HomeFragment
import com.google.android.libraries.places.api.Places

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Places API 초기화
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_appointments
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun getFriendsList(): List<Friend> {
        val homeFragment = supportFragmentManager.fragments.firstOrNull { it is HomeFragment } as? HomeFragment
        return homeFragment?.getFriendsList() ?: emptyList()
    }
}