package com.devlomi.tahaqqaqhadith.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.devlomi.tahaqqaqhadith.common.DataState
import com.devlomi.tahaqqaqhadith.data.HadithQueryPlaceholderDataSource
import com.devlomi.tahaqqaqhadith.usecase.FetchFakeHadiths
import com.devlomi.tahaqqaqhadith.usecase.GetFakeHadithFromCache
import com.devlomi.tahaqqaqhadith.usecase.SearchForHadith
import com.devlomi.tahaqqaqhadith.usecase.SetFakeHadithSeen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val searchUseCase: SearchForHadith,
    private val fetchFakeHadiths: FetchFakeHadiths,
    private val getLocalFakeHadithFromCache: GetFakeHadithFromCache,
    private val setFakeHadithSeen: SetFakeHadithSeen,
) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> get() = _state.asStateFlow()

    init {
        val randomInt = 0..<HadithQueryPlaceholderDataSource.placeholders.size
        val randomItem = HadithQueryPlaceholderDataSource.placeholders[randomInt.random()]
        _state.update { it.copy(queryPlaceholder = randomItem) }

        viewModelScope.launch {
            /*
            1. Get the local fake hadith from cache and update the state with it.
            2. If the local fake hadith was not exists, fetch the fake hadiths from the network
            3. Update the state with the local fake hadith from cache after fetching from network.
            //TODO PERHAPS IMPLEMENT A REPOSITORY INSTEAD?
             */
            getLocalFakeHadithFromCache.execute().collect { result ->
                //TODO HANDLE WHEN TO SHOW A NEW HADITH?
                _state.update { it.copy(fakeHadith = result) }
                if (result.isSuccess()) {
                    launch {

                        withContext(Dispatchers.IO) {
                            fetchFakeHadiths.execute().collect { remoteResult ->
                                val currentFakeHadith = state.value.fakeHadith
                                if (currentFakeHadith?.data != null) {
                                    return@collect
                                }
                                withContext(Dispatchers.Main) {
                                    _state.update { state ->
                                        state.copy(
                                            fakeHadith = state.fakeHadith?.copy(
                                                type = remoteResult.type
                                            )
                                        )
                                    }
                                    if (remoteResult.isSuccess()) {
                                        launch {
                                            getLocalFakeHadithFromCache.execute()
                                                .collect { newResult ->
                                                    _state.update { it.copy(fakeHadith = newResult) }
                                                    if (newResult.isSuccess() && newResult.data != null) {
                                                        setFakeHadithSeen.execute(
                                                            newResult.data.id,
                                                            true
                                                        ).collect { }
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: HomeEvents) {
        when (event) {
            is HomeEvents.Search -> {
                viewModelScope.launch {
                    val query = state.value.query.trim()
                    if (query.isEmpty()) return@launch
                    _state.update { it.copy(submittedSearchQuery = query) }
                    withContext(Dispatchers.IO) {
                        searchUseCase.execute(query).collect { result ->
                            withContext(Dispatchers.Main) {
                                _state.value = _state.value.copy(searchResult = result)
                            }
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