package com.example.openweatherapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.openweatherapp.BuildConfig
import com.example.openweatherapp.data.local.AppDatabase
import com.example.openweatherapp.data.model.WeatherEntity
import com.example.openweatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).weatherDao()

    private val repository = WeatherRepository(
        dao,
        BuildConfig.OPEN_WEATHER_API_KEY
    )

    private val _currentWeather = MutableStateFlow<WeatherEntity?>(null)
    val currentWeather: StateFlow<WeatherEntity?> = _currentWeather

    val history = repository.getAllHistory()

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            val result = repository.getWeather(city)
            _currentWeather.value = result
        }
    }
}