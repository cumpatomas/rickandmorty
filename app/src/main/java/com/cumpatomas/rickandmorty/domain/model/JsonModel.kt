package com.cumpatomas.rickandmorty.domain.model

import com.google.gson.annotations.SerializedName

data class JsonModel(
    var results: List<CharItem>?,
    var info: Pages
)

data class CharItem(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("species")
    val species: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("image")
    val image: String?
    )

data class Pages(
    @SerializedName("pages")
    val pages: Int
)

