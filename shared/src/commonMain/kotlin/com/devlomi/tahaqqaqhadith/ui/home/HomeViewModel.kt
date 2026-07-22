package com.devlomi.tahaqqaqhadith.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlomi.tahaqqaqhadith.data.HadithQueryPlaceholderDataSource
import com.devlomi.tahaqqaqhadith.usecase.SearchForHadith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val searchUseCase: SearchForHadith) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> get() = _state.asStateFlow()

    init {
        val randomInt = 0..HadithQueryPlaceholderDataSource.placeholders.size
        val randomItem = HadithQueryPlaceholderDataSource.placeholders[randomInt.random()]
        _state.update { it.copy(queryPlaceholder = randomItem) }
    }

    fun onEvent(event: HomeEvents) {
        when (event) {
            is HomeEvents.Search -> {
                viewModelScope.launch {
                    val query = state.value.query.trim()
                    if (query.isEmpty()) return@launch
                    _state.update { it.copy(submittedSearchQuery = query) }
                    searchUseCase.execute(query).collect { result ->
                        _state.value = _state.value.copy(isLoading = result.isLoading())
                        if (result.isSuccess() && result.data != null) {
                            _state.update { it.copy(data = result.data) }
                        }
                        if (result.isError() && result.message != null) {
                            //TODO HANDLE ERROR
                        }
                    }
                }
            }

            is HomeEvents.OnQueryTextChange -> {
                _state.update { it.copy(query = event.text) }
            }
        }
    }
}