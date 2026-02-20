package com.example.openweatherapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.openweatherapp.ViewModel.WeatherViewModel
import com.example.openweatherapp.data.model.WeatherEntity

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    var city by remember { mutableStateOf("") }

    val weather by viewModel.currentWeather.collectAsState()
    val history by viewModel.history.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {

        Text(
            text = "Sääsovellus",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Kaupunki") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.fetchWeather(city) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hae sää")
        }

        Spacer(modifier = Modifier.height(24.dp))

        weather?.let { data ->
            CurrentWeatherCard(data)
        }

        if (history.isNotEmpty()) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hakuhistoria",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            history.forEach { item ->
                HistoryItemCard(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(weather: WeatherEntity) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = weather.city,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${weather.temperature} °C",
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun HistoryItemCard(weather: WeatherEntity) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = weather.city,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = weather.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "${weather.temperature} °C",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}