package com.example.myapplication_test1.ui.home

import FriendAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.R
import com.example.myapplication_test1.data.Friend
import com.example.myapplication_test1.databinding.FragmentHomeBinding
import com.example.myapplication_test1.util.TypeMapper
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val friendsList = mutableListOf<Friend>()

    // ItemDecoration 클래스 추가
    private class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            // 마지막 아이템이 아닌 경우에만 간격 추가
            if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
                outRect.bottom = verticalSpaceHeight
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.pokeballImage.setOnClickListener {
            showAddFriendDialog()
        }

        val friendAdapter = FriendAdapter(friendsList)
        binding.recyclerViewFriends.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = friendAdapter
            addItemDecoration(VerticalSpaceItemDecoration(
                resources.getDimensionPixelSize(R.dimen.item_spacing)
            ))
        }

        return binding.root
    }

    private fun showAddFriendDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_friend)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val nameInput = dialog.findViewById<TextInputEditText>(R.id.nameInput)
        val birthYearInput = dialog.findViewById<TextInputEditText>(R.id.birthYearInput)
        val schoolInput = dialog.findViewById<TextInputEditText>(R.id.schoolInput)
        val mbtiInput = dialog.findViewById<TextInputEditText>(R.id.mbtiInput)
        val phoneInput = dialog.findViewById<TextInputEditText>(R.id.phoneInput)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)

        // MBTI 입력 시 자동으로 대문자 변환
        mbtiInput.doAfterTextChanged { text ->
            val newText = text.toString().uppercase()
            if (text.toString() != newText) {
                mbtiInput.setText(newText)
                mbtiInput.setSelection(newText.length)
            }
            if (newText.isNotEmpty() && !isValidMbti(newText)) {
                mbtiInput.error = "올바른 MBTI를 입력해주세요"
            }
        }

        // 출생년도 validation
        birthYearInput.doAfterTextChanged { text ->
            try {
                val year = text.toString().toInt()
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                if (year > currentYear) {
                    birthYearInput.error = "올바른 출생년도를 입력해주세요"
                }
            } catch (e: NumberFormatException) {
                if (text?.isNotEmpty() == true) {
                    birthYearInput.error = "숫자만 입력해주세요"
                }
            }
        }

        // 취소 버튼
        dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            showCustomToast("으악, 도망가버렸어!", true)
            dialog.dismiss()
        }

        // 등록 버튼
        confirmButton.setOnClickListener {
            when {
                nameInput.text.isNullOrEmpty() -> {
                    nameInput.error = "이름을 입력해주세요"
                    nameInput.requestFocus()
                }
                !isValidBirthYear(birthYearInput.text.toString()) -> {
                    birthYearInput.error = "올바른 출생년도를 입력해주세요"
                    birthYearInput.requestFocus()
                }
                schoolInput.text.isNullOrEmpty() -> {
                    schoolInput.error = "학교를 입력해주세요"
                    schoolInput.requestFocus()
                }
                !isValidMbti(mbtiInput.text.toString()) -> {
                    mbtiInput.error = "올바른 MBTI를 입력해주세요"
                    mbtiInput.requestFocus()
                }
                phoneInput.text.isNullOrEmpty() -> {
                    phoneInput.error = "전화번호를 입력해주세요"
                    phoneInput.requestFocus()
                }
                else -> {
                    val mbti = mbtiInput.text.toString().uppercase()
                    val pokemonType = TypeMapper.getTypeByMbti(mbti)

                    // 이미지 리소스 ID 선택
                    val imageResourceId = when (pokemonType.name) {
                        "땅" -> listOf(R.drawable.ground_1, R.drawable.ground_2).random()
                        "바위" -> listOf(R.drawable.rock_1, R.drawable.rock_2).random()
                        "에스퍼" -> listOf(R.drawable.phychic_1, R.drawable.phychic_2).random()
                        "악" -> listOf(R.drawable.dark_1, R.drawable.dark_2).random()
                        "노말" -> listOf(R.drawable.normal_1, R.drawable.normal_2).random()
                        "페어리" -> listOf(R.drawable.fairy_1, R.drawable.fairy_2).random()
                        "물" -> listOf(R.drawable.water_1, R.drawable.water_2).random()
                        "고스트" -> listOf(R.drawable.ghost_1, R.drawable.ghost_2).random()
                        "드래곤" -> listOf(R.drawable.dragon_1, R.drawable.dragon_2).random()
                        "전기" -> listOf(R.drawable.electric_1, R.drawable.electric_2).random()
                        "비행" -> listOf(R.drawable.flying_1, R.drawable.flying_2).random()
                        "격투" -> listOf(R.drawable.fighting_1, R.drawable.fighting_2).random()
                        "강철" -> listOf(R.drawable.steel_1, R.drawable.steel_2).random()
                        "풀" -> listOf(R.drawable.grass_1, R.drawable.grass_2).random()
                        "불꽃" -> listOf(R.drawable.fire_1, R.drawable.fire_2).random()
                        "독" -> listOf(R.drawable.poison_1, R.drawable.poison_2).random()
                        else -> R.drawable.placeholder_image
                    }

                    val friend = Friend(
                        name = nameInput.text.toString(),
                        birthYear = birthYearInput.text.toString(),
                        school = schoolInput.text.toString(),
                        mbti = mbti,
                        phoneNumber = phoneInput.text.toString(),
                        type = pokemonType.name,
                        backgroundColor = pokemonType.backgroundColor,
                        imageResourceId = imageResourceId  // 선택된 이미지 리소스 ID 저장
                    )

                    friendsList.add(friend)
                    showCustomToast("포켓몬 ${friend.name}을(를) 잡았다!", false)
                    dialog.dismiss()
                    // TODO: RecyclerView 업데이트 로직 추가
                }
            }
        }

        dialog.show()
    }

    private fun showCustomToast(message: String, isError: Boolean) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView = layout.findViewById<TextView>(R.id.toastText)
        textView.text = message
        textView.setTextColor(if (isError) Color.RED else Color.GREEN)

        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun isValidMbti(mbti: String): Boolean {
        val validMbti = mbti.uppercase()
        val validFirstLetters = listOf("I", "E")
        val validSecondLetters = listOf("S", "N")
        val validThirdLetters = listOf("T", "F")
        val validFourthLetters = listOf("J", "P")

        return validMbti.length == 4 &&
                validFirstLetters.contains(validMbti[0].toString()) &&
                validSecondLetters.contains(validMbti[1].toString()) &&
                validThirdLetters.contains(validMbti[2].toString()) &&
                validFourthLetters.contains(validMbti[3].toString())
    }

    private fun isValidBirthYear(year: String): Boolean {
        return try {
            val birthYear = year.toInt()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            birthYear in 1900..currentYear
        } catch (e: NumberFormatException) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}