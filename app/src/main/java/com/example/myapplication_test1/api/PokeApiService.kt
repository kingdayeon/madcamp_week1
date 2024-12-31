package com.example.myapplication_test1.api

import TypeListResponse
import com.example.myapplication_test1.data.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("type/{type}")
    suspend fun getPokemonsByType(@Path("type") type: String): TypeListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): PokemonResponse
}