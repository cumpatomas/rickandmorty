package com.cumpatomas.rickandmorty.domain.model

import com.google.gson.annotations.SerializedName

data class CharModel (
    val id: String,
    val name: String,
    val species: String,
    val gender: String,
    val url: String,
    val image: String
)