package com.example.myapplication_test1.data

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites
)

data class Sprites(
    @SerializedName("other")  // Gson이 JSON 필드명을 인식하게 해줌
    val other: Other
)

data class Other(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String   // 포켓몬 이미지 URL이 여기 담깁니다
)
