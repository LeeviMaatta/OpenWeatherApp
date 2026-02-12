# OpenWeatherApp

Yksinkertainen Android-sovellus, joka hakee säätiedot OpenWeather API:sta käyttäen Retrofitia, Coroutinesia, ViewModelia ja Jetpack Composea.

---

## Retrofit

Retrofit on HTTP-kirjasto Androidille, jonka avulla voidaan tehdä verkkopyyntöjä helposti ja selkeästi.

Tässä projektissa Retrofit:
* Tekee HTTP GET -pyynnön OpenWeather API:in
* Lisää URL-parametrit (q, appid, units, lang)
* Muuntaa API-vastauksen Kotlin-olioiksi

Esimerkki kutsusta:
```
@GET("weather")
suspend fun getWeatherByCity(
    @Query("q") city: String,
    @Query("appid") apiKey: String,
    @Query("units") units: String = "metric",
    @Query("lang") language: String = "fi"
): WeatherResponse
```

---

## JSON -> dataluokka

OpenWeather API palauttaa JSON-dataa.

Esimerkiksi:
```
{
  "name": "Helsinki",
  "main": {
    "temp": 5.2
  },
  "weather": [
    {
      "description": "kevyt sade"
    }
  ]
}
```
Tämä muunnetaan automaattisesti Kotlin-dataluokaksi:
```
data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>
)
```
### Gson hoitaa muunnoksen taustalla
Gson:
* Lukee JSON:n
* Etsii vastaavat kentät dataluokista
* Luo Kotlin-oliot automaattisesti

---

## Coroutines

API-kutsu tehdään taustasäikeessä coroutinesien avulla.
* viewModelScope.launch käynnistää coroutine-säikeen
* suspend-funktio odottaa verkkovastausta
* Pääsäie (UI) ei jää jumiin
* Kun data saapuu → UI päivittyy

---

## UI-tila

Sovellus käyttää yksisuuntaista datavirtaa (Unidirectional Data Flow).
```
data class WeatherUiState(
    val city: String = "",
    val weather: WeatherResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```
Compose reagoi tilamuutoksiin automaattisesti

Kun uiState muuttuu:
* Compose havaitsee muutoksen
* UI piirtyy uudelleen automaattisesti
* Ei tarvita manuaalista päivitystä

---

## API-avain

API-avain on tallennettu local.properties tiedostoon.
```
OPEN_WEATHER_API_KEY=your_api_key_here
```
Avain siirretään BuildConfigiin:
```
buildConfigField(
    "String",
    "OPEN_WEATHER_API_KEY",
    "\"${project.properties["OPEN_WEATHER_API_KEY"]}\""
)
```
Käyttö ViewModelissa:
```
BuildConfig.OPEN_WEATHER_API_KEY
```
