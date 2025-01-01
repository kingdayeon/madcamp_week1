package com.example.myapplication_test1

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication_test1.data.Friend
import com.example.myapplication_test1.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.example.myapplication_test1.ui.dashboard.DashboardFragment
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication_test1.ui.home.HomeFragment
import com.google.android.libraries.places.api.Places
import android.os.Build
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication_test1.adapter.ViewPagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_STORAGE_PERMISSION = 102

    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_STORAGE_PERMISSION)
                return false
            }
        }
        return true
    }

    companion object {
        private const val REQUEST_CODE_READ_STORAGE = 100 // 권한 요청 코드
    }

    //gallery launcher
    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val currentFragment = supportFragmentManager.fragments.firstOrNull { fragment ->
                fragment.javaClass == DashboardFragment::class.java
            } as? DashboardFragment
            currentFragment?.addImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Places API 초기화
        Places.initialize(applicationContext, getString(R.string.google_maps_key))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndRequestStoragePermission()

        // ViewPager2 설정
        val viewPager = binding.viewPager
        val navView = binding.navView

        // ViewPager 어댑터 설정
        viewPager.adapter = ViewPagerAdapter(this)

        // ViewPager 페이지 변경시 네비게이션 바 업데이트
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> navView.selectedItemId = R.id.navigation_home
                    1 -> navView.selectedItemId = R.id.navigation_dashboard
                    2 -> navView.selectedItemId = R.id.navigation_appointments
                }
            }
        })

        // 네비게이션 아이템 클릭시 ViewPager 페이지 변경
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> viewPager.currentItem = 0
                R.id.navigation_dashboard -> viewPager.currentItem = 1
                R.id.navigation_appointments -> viewPager.currentItem = 2
            }
            true
        }

        // 네비게이션 뷰 스타일 설정
        navView.apply {
            itemRippleColor = null
            itemBackground = null
            background = null
            elevation = 0f
        }
    }

    private fun checkAndRequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_STORAGE
            )
        } else {
            onStoragePermissionGranted()
        }
    }

    private fun onImageSelected(uri: Uri) {
        if (binding.viewPager.currentItem == 1) { // DashboardFragment의 position이 1
            val currentFragment = supportFragmentManager.fragments[0] as? DashboardFragment
            currentFragment?.addImage(uri)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onStoragePermissionGranted()
            } else {
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onStoragePermissionGranted() {
        Toast.makeText(this, "권한이 허용되었습니다!", Toast.LENGTH_SHORT).show()
    }

    fun getFriendsList(): List<Friend> {
        return HomeFragment.getFriendsList()
    }
}