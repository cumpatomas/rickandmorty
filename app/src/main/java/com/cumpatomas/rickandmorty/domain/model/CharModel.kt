package com.cumpatomas.rickandmorty.domain.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class CharModel (
    val id: String,
    val name: String,
    val species: String,
    val gender: String,
    val url: String,
    val image: String,
    var webViewState: MutableState<Boolean> = mutableStateOf(false)
)