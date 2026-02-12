package com.example.openweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.openweatherapp.ui.WeatherScreen
import com.example.openweatherapp.ui.theme.OpenWeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenWeatherAppTheme {
                WeatherScreen()
            }
        }
    }
}