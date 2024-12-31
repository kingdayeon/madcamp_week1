package com.example.myapplication_test1.ui.appointments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.myapplication_test1.AppointmentAdapter
import com.example.myapplication_test1.data.Appointment
import com.example.myapplication_test1.data.Friend
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AppointmentsFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!

    // 약속 데이터를 저장하는 리스트 선언
    private val appointmentsList = mutableListOf<Appointment>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appointmentAdapter: AppointmentAdapter

    private val PREFS_NAME = "AppointmentsPrefs"
    private val APPOINTMENTS_KEY = "appointmentsList"

    private var selectedLocation: LatLng? = null
    private var selectedLocationName: String? = null
    private var currentDialog: Dialog? = null

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        // SharedPreferences 초기화 및 데이터 로드
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        loadAppointments()

        binding.pokeballImage.setOnClickListener {
            // Step 1: 포켓볼 회전 및 깜빡이는 애니메이션 설정
            val rotateAnimation = ObjectAnimator.ofFloat(binding.pokeballImage, "rotation", 0f, 30f).apply {
                duration = 300
            }
            val darkenAnimation = ObjectAnimator.ofFloat(binding.pokeballImage, "alpha", 1f, 0.7f).apply {
                duration = 150
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = 1
            }

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(rotateAnimation, darkenAnimation)

            // Step 2: 애니메이션이 끝난 후 다이얼로그 표시
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    showAddAppointmentDialog() // 다이얼로그 표시

                    // 다이얼로그가 닫힐 때 다시 원래 상태로 돌아오는 애니메이션 실행
                    val returnRotateAnimation = ObjectAnimator.ofFloat(binding.pokeballImage, "rotation", 30f, 0f).apply {
                        duration = 300
                    }
                    val returnAlphaAnimation = ObjectAnimator.ofFloat(binding.pokeballImage, "alpha", 0.7f, 1f).apply {
                        duration = 150
                    }

                    val returnAnimatorSet = AnimatorSet()
                    returnAnimatorSet.playTogether(returnRotateAnimation, returnAlphaAnimation)

                    // 다이얼로그가 닫힌 후 실행되도록 Handler로 지연 처리
                    Handler(Looper.getMainLooper()).postDelayed({
                        returnAnimatorSet.start()
                    }, 500) // 다이얼로그가 종료될 때까지 기다리는 시간
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })

            // Step 3: 애니메이션 시작
            animatorSet.start()
        }

        // RecyclerView 초기화
        appointmentAdapter = AppointmentAdapter(
            appointmentsList,  // MutableList로 선언된 리스트
            onLocationClick = { latLng ->  // 위치 클릭 콜백
                val gmmIntentUri = Uri.parse("geo:${latLng.latitude},${latLng.longitude}?z=16")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                startActivity(mapIntent)
            },
            onDeleteClick = { position ->  // 삭제 버튼 클릭 콜백
                appointmentsList.removeAt(position)
                appointmentAdapter.notifyItemRemoved(position)
                appointmentAdapter.notifyItemRangeChanged(position, appointmentsList.size)
                saveAppointments() // 데이터 저장
            }
        )

        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }

        return binding.root
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

        // 위치 정보 초기화
        selectedLocation = null
        selectedLocationName = null
        locationInput?.setText("포켓스톱")  // 기본값으로 설정


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
                        date = selectedDate,
                        title = titleInput?.text.toString(),
                        friends = friendList,
                        location = selectedLocationName ?: "포켓스톱",
                        locationLatLng = if (selectedLocation != null && selectedLocationName != null) selectedLocation else null,
                        memo = memoInput?.text.toString()
                    )
                    // 리스트에 추가
                    appointmentsList.add(newAppointment)

                    // 날짜순 정렬
                    appointmentsList.sortBy { it.date }

                    // 어댑터에 변경 알림
                    appointmentAdapter.notifyDataSetChanged()
                    saveAppointments() // 데이터 저장
                    // 다이얼로그 닫기
                    currentDialog?.dismiss()
                }
            }
        }

        currentDialog?.show()
    }

    private fun saveAppointments() {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(appointmentsList)
        editor.putString(APPOINTMENTS_KEY, json)
        editor.apply()
    }

    private fun loadAppointments() {
        val json = sharedPreferences.getString(APPOINTMENTS_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Appointment>>() {}.type
            val savedAppointments: MutableList<Appointment> = Gson().fromJson(json, type)
            appointmentsList.clear()
            appointmentsList.addAll(savedAppointments)
        }
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