package com.example.openweatherapp.data.repository

import com.example.openweatherapp.data.local.WeatherDao
import com.example.openweatherapp.data.model.WeatherEntity
import com.example.openweatherapp.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val dao: WeatherDao,
    private val apiKey: String
) {
    companion object {
        private const val CACHE_DURATION = 30 * 60 * 1000 // 30 min
    }

    fun getAllHistory(): Flow<List<WeatherEntity>> =
        dao.getAllWeather()

    suspend fun getWeather(city: String): WeatherEntity {

        val cached = dao.getWeatherByCity(city)

        val now = System.currentTimeMillis()

        if (cached != null && (now - cached.lastUpdated) < CACHE_DURATION) {
            return cached
        }

        val response = RetrofitInstance.api.getWeatherByCity(
            city = city,
            apiKey = apiKey
        )

        val entity = WeatherEntity(
            city = response.name,
            temperature = response.main.temp,
            description = response.weather.firstOrNull()?.description ?: "",
            lastUpdated = now
        )

        dao.insertWeather(entity)

        return entity
    }
}