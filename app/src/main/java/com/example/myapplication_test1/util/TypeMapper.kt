package com.example.myapplication_test1.util

data class PokemonType(
    val name: String,
    val color: String,  // 뱃지 색상
    val backgroundColor: String  // 배경 원 색상 (50% 투명도)
)

object TypeMapper {
    private val mbtiToType = mapOf(
        "ISTJ" to PokemonType("땅", "#ab7939", "#ab793980"),
        "ISFJ" to PokemonType("바위", "#bcb889", "#bcb88980"),
        "INFJ" to PokemonType("에스퍼", "#ff637f", "#ff637f80"),
        "INTJ" to PokemonType("악", "#4f4747", "#4f474780"),
        "ISTP" to PokemonType("노말", "#999999", "#99999980"),
        "ISFP" to PokemonType("페어리", "#ffb1ff", "#ffb1ff80"),
        "INFP" to PokemonType("물", "#2992ff", "#2992ff80"),
        "INTP" to PokemonType("고스트", "#6e4570", "#6e457080"),
        "ESTP" to PokemonType("드래곤", "#5462d6", "#5462d680"),
        "ESFP" to PokemonType("전기", "#ffdb00", "#ffdb0080"),
        "ENFP" to PokemonType("비행", "#95c9ff", "#95c9ff80"),
        "ENTP" to PokemonType("격투", "#ffa202", "#ffa20280"),
        "ESTJ" to PokemonType("강철", "#6aaed3", "#6aaed380"),
        "ESFJ" to PokemonType("풀", "#42bf24", "#42bf2480"),
        "ENFJ" to PokemonType("불꽃", "#ff612c", "#ff612c80"),
        "ENTJ" to PokemonType("독", "#994dcf", "#994dcf80")
    )

    fun getTypeByMbti(mbti: String): PokemonType {
        return mbtiToType[mbti.uppercase()] ?: PokemonType("노말", "#999999", "#99999980")
    }
}