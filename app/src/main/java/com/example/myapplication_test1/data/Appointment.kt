package com.example.myapplication_test1.data
import com.google.android.gms.maps.model.LatLng

data class Appointment(
    val title: String,        // 약속 제목
    val date: Long,           // 약속 날짜
    val location: String,     // 약속 장소
    val locationLatLng: LatLng?, // 구글 맵 좌표
    val friends: List<Friend>, // 선택된 친구들
    val memo: String          // 메모
)