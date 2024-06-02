package com.example.androidgithubactionssample.ui.screen.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidgithubactionssample.model.api.ResponseLocationData
import com.example.androidgithubactionssample.model.api.toDomainModel
import com.example.androidgithubactionssample.model.domain.LocationData
import com.example.androidgithubactionssample.repository.GeoLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DisplayScreenViewModel
    @Inject
    constructor(
        private val repository: GeoLocationRepository,
    ) : ViewModel() {
        private val _result = MutableStateFlow(LocationData("", emptyMap(), "0", "0", ""))
        val result = _result.asStateFlow()

        private val _loading = MutableStateFlow(false)
        val loading = _loading.asStateFlow()

        fun searchLocation(q: String) {
            viewModelScope.launch {
                _loading.value = true
                try {
                    val locationResponse = getLocation(q)
                    handleLocationResponse(locationResponse)
                } catch (e: Exception) {
                    _result.value = LocationData("", emptyMap(), "0", "0", "")
                    _loading.value = false
                }
            }
        }

        private fun handleLocationResponse(response: Response<List<ResponseLocationData>>?) {
            if (response?.isSuccessful == true) {
                val locationData = response.body()?.firstOrNull()?.toDomainModel()
                _result.value = locationData ?: LocationData("", emptyMap(), "0", "0", "")
            }
            _loading.value = false
        }

        private suspend fun getLocation(q: String): Response<List<ResponseLocationData>>? {
            return repository.execute(q)
        }
    }
