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
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_test1.R
import com.example.myapplication_test1.data.Friend
import com.example.myapplication_test1.databinding.FragmentHomeBinding
import com.example.myapplication_test1.util.TypeMapper
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication_test1.api.PokeApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import coil.load
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
//    private val friendsList = mutableListOf<Friend>()
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val PREFS_NAME = "FriendPrefs"
    private val FRIENDS_KEY = "friendsList"

    // ItemDecoration 클래스 추가
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)



        loadFriends() // load saved list

        friendAdapter = FriendAdapter(_friendsList) { friend ->
            removeFriend(friend)
        }

        binding.recyclerViewFriends.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = friendAdapter
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_spacing)
                )
            )
        }

        binding.pokeballImage.setOnClickListener {
            showAddFriendDialog()
        }

        return binding.root
    }

    private suspend fun getRandomPokemonImageByType(type: String): String? {
        try {
            // 1. 해당 타입의 모든 포켓몬 리스트를 가져옵니다
            val typeResponse = PokeApiClient.service.getPokemonsByType(type)

            // 2. 포켓몬 리스트에서 랜덤으로 하나를 선택합니다
            val randomPokemon = typeResponse.pokemon.random().pokemon

            // 3. URL에서 포켓몬 ID를 추출합니다
            // URL 형식: "https://pokeapi.co/api/v2/pokemon/25/"
            val pokemonId = randomPokemon.url.split("/").dropLast(1).last().toInt()

            // 4. 선택된 포켓몬의 상세 정보를 가져옵니다
            val pokemonResponse = PokeApiClient.service.getPokemonById(pokemonId)

            // 5. 공식 아트워크 이미지 URL을 반환합니다
            return pokemonResponse.sprites.other.officialArtwork.frontDefault
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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

        dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            showCustomToast("으악, 도망가버렸어!", true)
            dialog.dismiss()
        }

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
                    // MBTI로 포켓몬 타입 가져오기
                    val mbti = mbtiInput.text.toString().uppercase()
                    val pokemonType = TypeMapper.getTypeByMbti(mbti)

                    // 코루틴 스코프 실행
                    lifecycleScope.launch {
                        // 해당 타입의 랜덤 포켓몬 이미지 URL 가져오기
                        val imageUrl = getRandomPokemonImageByType(pokemonType.nameEn)

                        if (imageUrl != null) {
                            // Friend 객체 생성 (imageResourceId 대신 imageUrl 사용)
                            val friend = Friend(
                                name = nameInput.text.toString(),
                                birthYear = birthYearInput.text.toString(),
                                school = schoolInput.text.toString(),
                                mbti = mbti,
                                phoneNumber = phoneInput.text.toString(),
                                type = pokemonType.name,
                                backgroundColor = pokemonType.backgroundColor,
                                imageUrl = imageUrl  // 여기가 변경됨
                            )

                            _friendsList.add(friend)
                            saveFriends()
                            friendAdapter.notifyDataSetChanged()
                            showCustomToast("포켓몬 ${friend.name}을(를) 잡았다!", false)
                            dialog.dismiss()
                        } else {
                            showCustomToast("포켓몬을 잡는데 실패했습니다.", true)
                        }
                    }
                }
            }
        }

        dialog.show()
    }
    private fun saveFriends() {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(_friendsList)
        editor.putString(FRIENDS_KEY, json)
        editor.apply()
    }

    private fun loadFriends() {
        val json = sharedPreferences.getString(FRIENDS_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Friend>>() {}.type
            val savedFriends: MutableList<Friend> = Gson().fromJson(json, type)
            _friendsList.clear()
            _friendsList.addAll(savedFriends)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCustomToast(message: String, isError: Boolean) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView = layout.findViewById<TextView>(R.id.toastText)
        textView.text = message
        textView.setTextColor(if (isError) Color.RED else Color.parseColor("#006400"))

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



    private fun removeFriend(friend: Friend) {
        _friendsList.remove(friend)
        friendAdapter.notifyDataSetChanged()
        saveFriends()
    }

    companion object {
        private val _friendsList = mutableListOf<Friend>()

        fun addFriend(friend: Friend) {
            _friendsList.add(friend)
        }

        fun removeFriend(friend: Friend) {
            _friendsList.remove(friend)
        }

        fun getFriendsList(): List<Friend> {
            return _friendsList.toList()
        }
    }
    }
