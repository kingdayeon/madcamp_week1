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
            val DashboardFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            (DashboardFragment?.childFragmentManager?.fragments?.get(0) as? DashboardFragment)?.addImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // ActionBar 숨기기

        // Places API 초기화
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndRequestStoragePermission()

        val navView: BottomNavigationView = binding.navView
        navView.itemRippleColor = null
        navView.itemBackground = null

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

    private fun checkAndRequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 허용되지 않은 경우 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_STORAGE
            )
        } else {
            // 권한이 이미 허용된 경우
            onStoragePermissionGranted()
        }
    }

    private fun onImageSelected(uri: Uri) {
        // Pass the image URI to `DashboardFragment`
        val dashboardFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        (dashboardFragment?.childFragmentManager?.fragments?.get(0) as? DashboardFragment)?.addImage(uri)
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용됨
                onStoragePermissionGranted()
            } else {
                // 권한 거부됨
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 권한이 허용된 후 실행할 작업
    private fun onStoragePermissionGranted() {
        Toast.makeText(this, "권한이 허용되었습니다!", Toast.LENGTH_SHORT).show()
        // 갤러리 열기 또는 다른 작업 실행
    }

    fun getFriendsList(): List<Friend> {
        val homeFragment = supportFragmentManager.fragments.firstOrNull { it is HomeFragment } as? HomeFragment
        return homeFragment?.getFriendsList() ?: emptyList()
    }
}
