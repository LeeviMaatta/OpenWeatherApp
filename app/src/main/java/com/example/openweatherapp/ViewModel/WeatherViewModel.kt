package com.example.openweatherapp.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openweatherapp.BuildConfig
import com.example.openweatherapp.data.model.WeatherResponse
import com.example.openweatherapp.data.remote.RetrofitInstance
import kotlinx.coroutines.launch

data class WeatherUiState(
    val city: String = "",
    val weather: WeatherResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {
    var uiState by mutableStateOf(WeatherUiState())
        private set

    fun updateCity(newCity: String) {
        uiState = uiState.copy(city = newCity)
    }

    fun fetchWeather() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = RetrofitInstance.api.getWeatherByCity(
                    city = uiState.city,
                    apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                )
                uiState = uiState.copy(
                    weather = response,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Virhe haettaessa säätä", e)
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Virhe haettaessa säätä"
                )
            }
        }
    }
}