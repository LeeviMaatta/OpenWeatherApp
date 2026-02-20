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

---

## Room

Room on Androidin tietokantakirjasto, joka tarjoaa abstraktiokerroksen SQLite-tietokannan päälle. Se helpottaa datan tallentamista ja lukemista turvallisesti ja selkeästi.

Tässä projektissa Room koostuu seuraavista osista:

### Entity

Määrittelee tietokantataulun rakenteen.
WeatherEntity kuvaa tallennettavan sään:
* city
* temperature
* description
* lastUpdated

### DAO (Data Access Object)

Sisältää SQL-kyselyt ja tietokantaoperaatiot.
WeatherDao:
* hakee sään kaupungin perusteella
* palauttaa hakuhistorian Flow-listana
* tallentaa uuden säädatan

### Database

AppDatabase yhdistää Entityt ja DAO:t yhdeksi Room-tietokannaksi.

### Repository

WeatherRepository toimii välikerroksena:
* päättää haetaanko data Roomista vai API:sta
* kapseloi välimuistilogiikan
* tarjoaa ViewModelille suspend- ja Flow-funktiot

### ViewModel

WeatherViewModel:
* kutsuu Repositorya
* altistaa datan UI:lle StateFlow-muodossa
* huolehtii korutiineista

### UI (Jetpack Compose)

* Kuuntelee ViewModelin tilaa collectAsState() avulla
* Näyttää nykyisen sään
* Näyttää hakuhistorian
* Kaikki data tulee Roomin kautta

---

## Projekti rakenne

```
data/
 ├── model/
 │    ├── WeatherEntity.kt
 │    └── WeatherResponse.kt
 │
 ├── local/
 │    ├── WeatherDao.kt
 │    └── AppDatabase.kt
 │
 ├── remote/
 │    ├── WeatherApi.kt
 │    └── RetrofitInstance.kt
 │
 └── repository/
      └── WeatherRepository.kt

viewmodel/
 └── WeatherViewModel.kt

ui/
 ├── WeatherScreen.kt
 └── UI-komponentit
```

---

## Miten datavirta kulkee?

1. Käyttäjä syöttää kaupungin UI:ssa
2. UI kutsuu ViewModelin fetchWeather()
3. ViewModel kutsuu Repositorya
4. Repository:
* tarkistaa Roomista onko data olemassa ja ajantasainen
* jos ei → hakee API:sta ja tallentaa Roomiin
5. Room päivittää Flow-datan
6. ViewModel välittää datan UI:lle
7. Compose UI päivittyy automaattisesti

Data kulkee aina Roomin kautta UI:lle.

---

## Välimuistilogiikka

Sovellus käyttää 30 minuutin välimuistia.

Repository tarkistaa:
```
jos (nykyinen aika - lastUpdated) < 30 min
    → käytä Roomissa olevaa dataa
muuten
    → hae uusi data OpenWeather API:sta
    → tallenna se Roomiin
```
Tämän ansiosta:
* turhia API-kutsuja vältetään
* sovellus toimii nopeammin
* hakuhistoria säilyy paikallisesti
