package com.devlomi.tahaqqaqhadith.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlomi.tahaqqaqhadith.usecase.SearchForHadith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val searchUseCase: SearchForHadith) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> get() = _state.asStateFlow()

    fun onEvent(event: HomeEvents) {
        when (event) {
            is HomeEvents.Search -> {
                viewModelScope.launch {
                    searchUseCase.execute(event.query).collect { result ->
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
        }
    }
}