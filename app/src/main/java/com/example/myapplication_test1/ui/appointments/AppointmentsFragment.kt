package com.example.myapplication_test1.ui.appointments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.app.Activity
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
import com.example.myapplication_test1.R
import com.example.myapplication_test1.databinding.FragmentAppointmentsBinding
import com.example.myapplication_test1.ui.home.HomeFragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar

class AppointmentsFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private var selectedLocation: LatLng? = null
    private var selectedLocationName: String? = null
    private var currentDialog: Dialog? = null

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)

        binding.pokeballImage.setOnClickListener {
            showAddAppointmentDialog()
        }

        return binding.root
    }

    private fun showAddAppointmentDialog() {
        currentDialog = Dialog(requireContext())
        currentDialog?.setContentView(R.layout.dialog_add_appointment)
        currentDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 친구 선택 드롭다운 설정
        val friendInput = currentDialog?.findViewById<AutoCompleteTextView>(R.id.friendInput)
        val friends = HomeFragment.getFriendsList()
        val friendAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            friends.map { "${it.name} (${it.school})" }
        )
        friendInput?.setAdapter(friendAdapter)

        // 각 입력 필드 참조
        val titleInput = currentDialog?.findViewById<TextInputEditText>(R.id.titleInput)
        val dateInput = currentDialog?.findViewById<TextInputEditText>(R.id.dateInput)
        val locationInput = currentDialog?.findViewById<TextInputEditText>(R.id.locationInput)
        val memoInput = currentDialog?.findViewById<TextInputEditText>(R.id.memoInput)
        val confirmButton = currentDialog?.findViewById<Button>(R.id.confirmButton)
        val cancelButton = currentDialog?.findViewById<Button>(R.id.cancelButton)

        // 날짜 선택 리스너
        dateInput?.setOnClickListener {
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

        // 위치 선택 리스너
        locationInput?.setOnClickListener {
            try {
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
                )
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields
                )
                    .setCountry("KR")
                    .build(requireContext())
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: Exception) {
                Toast.makeText(context, "구글 맵을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 취소/등록 버튼 리스너
        cancelButton?.setOnClickListener {
            currentDialog?.dismiss()
        }

        confirmButton?.setOnClickListener {
            when {
                titleInput?.text.isNullOrEmpty() -> {
                    titleInput?.error = "약속 제목을 입력해주세요"
                    titleInput?.requestFocus()
                }
                dateInput?.text.isNullOrEmpty() -> {
                    dateInput?.error = "날짜를 선택해주세요"
                    dateInput?.requestFocus()
                }
                else -> {
                    // TODO: 약속 생성 로직 구현
                    currentDialog?.dismiss()
                }
            }
        }

        currentDialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        selectedLocation = place.latLng
                        selectedLocationName = place.name
                        currentDialog?.findViewById<TextInputEditText>(R.id.locationInput)?.setText(place.name)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(context, "오류: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // 사용자가 취소한 경우
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}