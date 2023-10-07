package com.cumpatomas.rickandmorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cumpatomas.rickandmorty.domain.GetCharacters
import com.cumpatomas.rickandmorty.domain.model.CharModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    private val getCharList = GetCharacters()
    private val _charList = MutableStateFlow(emptySet<CharModel>())
    val charList = _charList.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _searchText = MutableStateFlow("")
    var searchText = _searchText.asStateFlow()
    private val _noResultsMessage = MutableStateFlow(false)
    val noResultsMessage = _noResultsMessage.asStateFlow()
    private var searchJob: Job? = null

    init {
        viewModelScope.launch(IO) {
            _loading.value = true
            launch {
                _charList.value = getCharList.invoke("")
            }.join()
            _loading.value = false
            if (_charList.value.isEmpty()) {
                _noResultsMessage.value = true
            }
        }
    }

    private suspend fun searchInList(query: String) {
        _charList.value = emptySet()

        _noResultsMessage.value = false
        viewModelScope.launch(IO) {
            _loading.value = true
            launch {
                if (query.isEmpty()) {
                    _charList.value = getCharList.invoke("")
                } else {
                    _charList.value = getCharList.invoke(query)
                }
            }.join()
            _loading.value = false
            if (_charList.value.isEmpty()) {
                _noResultsMessage.value = true
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun onSearchTextChange(query: String) {
        searchJob?.cancel()
        _searchText.value = query
        searchJob = viewModelScope.launch(IO) {
            searchText.debounce(1000).collect() {
                searchInList(query)
            }
        }
    }
}