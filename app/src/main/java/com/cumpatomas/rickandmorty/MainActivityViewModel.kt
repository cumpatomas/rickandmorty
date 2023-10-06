package com.cumpatomas.rickandmorty

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cumpatomas.rickandmorty.domain.GetCharacters
import com.cumpatomas.rickandmorty.domain.model.CharModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    private val getCharList = GetCharacters()
    private val _charList = MutableStateFlow(emptyList<CharModel>())
    val charList = _charList.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    val searchText = mutableStateOf("")
    private val _errorOccurred = MutableStateFlow(false)
    val errorOccurred = _errorOccurred.asStateFlow()

    init {
        viewModelScope.launch(IO) {
            _loading.value = true
            launch {
                _charList.value = getCharList.invoke("")
            }.join()
            _loading.value = false
            if (_charList.value.isEmpty()) {
                _errorOccurred.value = true
                println("ERROR!")
            }
        }
    }

    fun searchInList(query: String) {
        _errorOccurred.value = false
        viewModelScope.launch(IO) {
            _loading.value = true
            launch {
                if (query.isNotEmpty()) {
                    delay(1000)
                    _charList.value = getCharList.invoke()
                        .filter { it.name.lowercase().contains(query.lowercase()) }
                } else {
                    _charList.value = getCharList.invoke()
                }
            }.join()
            _loading.value = false
            if (_charList.value.isEmpty()) {
                _errorOccurred.value = true
                println("ERROR!")
            }
        }
    }
}