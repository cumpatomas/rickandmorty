package com.cumpatomas.rickandmorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cumpatomas.rickandmorty.domain.GetCharacters
import com.cumpatomas.rickandmorty.domain.model.CharModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    private val getCharList = GetCharacters()
    private val _charList = MutableStateFlow(emptySet<CharModel>())
    val charList = _charList.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _searchText = MutableStateFlow("")
    var searchText = _searchText.asStateFlow()
    private val _errorOccurred = MutableStateFlow(false)
    val errorOccurred = _errorOccurred.asStateFlow()
    private var searchJob: Job? = null

    init {
        viewModelScope.launch(IO) {
            _loading.value = true
            searchJob = launch {
                _charList.value = getCharList.invoke("")
            }
            searchJob?.join()
            _loading.value = false
            if (_charList.value.isEmpty()) {
                _errorOccurred.value = true
            }
        }
    }

    private fun searchInList(query: String) {
        searchJob?.cancel()
        _errorOccurred.value = false
        viewModelScope.launch(IO) {
            _loading.value = true
            launch {
                if (query.isEmpty()) {
                    _charList.value = getCharList.invoke("")
                } else {
                    delay(500)
                    if (query.length > 2)
                        _charList.value = emptySet()
                    _charList.value = getCharList.invoke(query)
                    println(_charList.value)
                }
            }.join()
            _loading.value = false
            if (_charList.value.isEmpty()) {
                _errorOccurred.value = true
            }
        }
    }

    fun onSearchTextChange(query: String) {
        _searchText.value = query
        searchInList(query)
    }
}