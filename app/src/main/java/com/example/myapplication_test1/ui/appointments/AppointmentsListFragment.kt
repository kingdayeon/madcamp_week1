package com.example.myapplication_test1.ui.appointments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.AppointmentAdapter
import com.example.myapplication_test1.R
import com.example.myapplication_test1.data.Appointment
import com.example.myapplication_test1.databinding.FragmentAppointmentsBinding
import android.app.DatePickerDialog
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.example.myapplication_test1.ui.appointments.AppointmentsFragment.Companion
import com.example.myapplication_test1.ui.home.HomeFragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import com.google.android.gms.maps.model.LatLng



class AppointmentsListFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private val appointmentsList = mutableListOf<Appointment>()
    private lateinit var appointmentAdapter: AppointmentAdapter

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

        setupRecyclerView()
        setupClickListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(
            appointmentsList,  // 이미 MutableList로 선언되어 있음
            onLocationClick = { latLng ->
                val gmmIntentUri = Uri.parse("geo:${latLng.latitude},${latLng.longitude}?z=16")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                startActivity(mapIntent)
            },
            onDeleteClick = { position ->
                appointmentsList.removeAt(position)
                appointmentAdapter.notifyItemRemoved(position)
                appointmentAdapter.notifyItemRangeChanged(position, appointmentsList.size)
            }
        )

        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
            addItemDecoration(VerticalSpaceItemDecoration(
                resources.getDimensionPixelSize(R.dimen.item_spacing)
            ))
        }
    }

    private var selectedDate: Long = 0L
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
                    // 선택된 날짜를 설정
                    val calendarSelected = Calendar.getInstance().apply {
                        set(selectedYear, month, day)
                    }
                    selectedDate = calendarSelected.timeInMillis

                    // 선택된 날짜를 입력 필드에 표시
                    dateInput.setText("${selectedYear}년 ${month + 1}월 ${day}일")
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
                startActivityForResult(intent, AppointmentsFragment.AUTOCOMPLETE_REQUEST_CODE)
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
                    // 친구 선택 처리 로직 추가
                    val selectedFriendText = friendInput?.text.toString()
                    val friendName = selectedFriendText.split(" (")[0] // 괄호 앞의 이름만 추출
                    val friendList = if (selectedFriendText.isNotEmpty()) {
                        HomeFragment.getFriendsList().filter { it.name == friendName }
                    } else {
                        emptyList()
                    }

                    // 약속 데이터 생성
                    val newAppointment = Appointment(
                        date = selectedDate, // 선택된 날짜
                        title = titleInput?.text.toString(),
                        friends = friendList, // 선택한 친구 리스트 추가
                        location = selectedLocationName ?: "위치 미정",
                        locationLatLng = selectedLocation,
                        memo = memoInput?.text.toString()
                    )

                    // 리스트에 추가
                    appointmentsList.add(newAppointment)

                    // 날짜순 정렬
                    appointmentsList.sortBy { it.date }

                    // 어댑터에 변경 알림
                    appointmentAdapter.notifyDataSetChanged()

                    // 다이얼로그 닫기
                    currentDialog?.dismiss()
                }
            }
        }

        currentDialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppointmentsFragment.AUTOCOMPLETE_REQUEST_CODE) {
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

    private fun setupClickListeners() {
        binding.pokeballImage.setOnClickListener {
            showAddAppointmentDialog()
        }
    }

    // VerticalSpaceItemDecoration 클래스 추가
    private class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
                outRect.bottom = verticalSpaceHeight
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}