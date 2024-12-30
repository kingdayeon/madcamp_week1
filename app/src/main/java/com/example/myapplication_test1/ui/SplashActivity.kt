package com.example.myapplication_test1.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication_test1.MainActivity
import com.example.myapplication_test1.databinding.ActivitySplashBinding
import com.example.myapplication_test1.R // R 파일을 명시적으로 import

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 2000 // 3초 타임아웃 설정

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 먼저 Splash 테마를 적용 (super.onCreate 전에 호출)
        setTheme(R.style.Theme_Splash)

        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        // 3초 뒤 MainActivity로 전환
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // 현재 액티비티 종료
        }, SPLASH_TIME_OUT)
    }
}
