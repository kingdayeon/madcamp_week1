package com.example.myapplication_test1.ui.appointments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
//import androidx.media3.common.util.Log
import com.example.myapplication_test1.MainActivity
import com.example.myapplication_test1.R
import com.example.myapplication_test1.databinding.FragmentAppointmentsBinding  // 여기 변경
import com.example.myapplication_test1.ui.home.HomeFragment
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AppointmentsFragment : Fragment() {

    private var _binding: FragmentAppointmentsBinding? = null  // 여기 변경
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)

        // 포켓볼 이미지 클릭 리스너 설정
        binding.pokeballImage.setOnClickListener {
            showAddAppointmentDialog()
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddAppointmentDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_appointment) // 방금 만든 레이아웃
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 친구 선택 드롭다운 설정
        val friendInput = dialog.findViewById<AutoCompleteTextView>(R.id.friendInput)

// HomeFragment의 companion object를 통해 직접 친구 목록을 가져옴
        val friends = HomeFragment.getFriendsList()

        Log.d("AppointmentsFragment", "Friends size: ${friends.size}")

        val friendAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,  // 기본 드롭다운 레이아웃 사용
            friends.map { "${it.name} (${it.school})" }
        )

        friendInput.setAdapter(friendAdapter)


        // 각 입력 필드 참조
        val titleInput = dialog.findViewById<TextInputEditText>(R.id.titleInput)
        val dateInput = dialog.findViewById<TextInputEditText>(R.id.dateInput)
        val locationInput = dialog.findViewById<TextInputEditText>(R.id.locationInput)

        val memoInput = dialog.findViewById<TextInputEditText>(R.id.memoInput)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        // 날짜 선택 클릭 리스너
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, selectedYear, month, day ->
                    val selectedDate = "${selectedYear}년 ${month + 1}월 ${day}일"
                    dateInput.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // 위치 선택 클릭 리스너
        locationInput.setOnClickListener {
            // 여기에 구글 맵 실행 코드가 들어갈 예정
            // 지금은 임시로 토스트 메시지만 표시
            Toast.makeText(context, "위치 선택 기능 구현 예정", Toast.LENGTH_SHORT).show()
        }

        // 취소 버튼
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // 등록 버튼
        confirmButton.setOnClickListener {
            // 필수 입력값 검증
            when {
                titleInput.text.isNullOrEmpty() -> {
                    titleInput.error = "약속 제목을 입력해주세요"
                    titleInput.requestFocus()
                }
                dateInput.text.isNullOrEmpty() -> {
                    dateInput.error = "날짜를 선택해주세요"
                    dateInput.requestFocus()
                }
                else -> {
                    // 약속 생성 로직
                    // 추후 구현 예정
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}

