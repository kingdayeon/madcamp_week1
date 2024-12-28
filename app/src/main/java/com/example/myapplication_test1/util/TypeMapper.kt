package com.example.myapplication_test1.util

data class PokemonType(
    val name: String,
    val color: String,  // 뱃지 색상
    val backgroundColor: String  // 배경 원 색상 (50% 투명도)
)

object TypeMapper {
    private val mbtiToType = mapOf(
        "ISTJ" to PokemonType("땅", "#ab7939", "#eac7a3"),
        "ISFJ" to PokemonType("바위", "#bcb889", "#f0e8c8"),
        "INFJ" to PokemonType("에스퍼", "#ff637f", "#ffc0cc"),
        "INTJ" to PokemonType("악", "#4f4747", "#a79e9e"),
        "ISTP" to PokemonType("노말", "#999999", "#e0e0e0"),
        "ISFP" to PokemonType("페어리", "#ffb1ff", "#ffe6ff"),
        "INFP" to PokemonType("물", "#2992ff", "#add6ff"),
        "INTP" to PokemonType("고스트", "#6e4570", "#c2a2c2"),
        "ESTP" to PokemonType("드래곤", "#5462d6", "#b8c5f3"),
        "ESFP" to PokemonType("전기", "#ffdb00", "#fff4b3"),
        "ENFP" to PokemonType("비행", "#95c9ff", "#e2f2ff"),
        "ENTP" to PokemonType("격투", "#ffa202", "#ffdeb3"),
        "ESTJ" to PokemonType("강철", "#6aaed3", "#c8e4f2"),
        "ESFJ" to PokemonType("풀", "#42bf24", "#b3f0a3"),
        "ENFJ" to PokemonType("불꽃", "#ff612c", "#ffbea0"),
        "ENTJ" to PokemonType("독", "#994dcf", "#e3c7f3")
    )



    fun getTypeByMbti(mbti: String): PokemonType {
        return mbtiToType[mbti.uppercase()] ?: PokemonType("노말", "#999999", "#99999980")
    }
}