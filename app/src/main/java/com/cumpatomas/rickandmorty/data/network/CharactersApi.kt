package com.cumpatomas.rickandmorty.data.network

import com.cumpatomas.rickandmorty.domain.model.JsonModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface CharactersApi {
    @GET
    suspend fun getChars(
        @Url url: String
    ): Response<JsonModel>
}
