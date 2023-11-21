package com.example.weather

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.appcompat.widget.SearchView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val API_ID = "02f857219591f007180146e52c001de9"

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("Delhi")
        searchCity()

    }

    private fun searchCity() {
         val searchView = binding.searchCity
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build().create(WeatherInterface::class.java)

        val response = retrofit.getWeatherData(cityName, API_ID, "metric")
        response.enqueue(object : Callback<WeatherApp>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {

                val responseBody = response.body()
                if (response.isSuccessful){
                    val temperature = responseBody?.main?.temp
                    val humidity = responseBody?.main?.humidity
                    val windSpeed = responseBody?.wind?.speed
                    val sunRise = responseBody?.sys?.sunrise?.toLong()
                    val sunSet = responseBody?.sys?.sunset?.toLong()
                    val seaLevel = responseBody?.main?.pressure
                    val condition = responseBody?.weather?.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody?.main?.temp_max
                    val minTemp = responseBody?.main?.temp_min

                    binding.tvTemperature.text = "$temperature°C"
                    binding.tvHumidity.text = "$humidity%"
                    binding.tvCondition.text = "$condition"
                    binding.tvDayType.text = "$condition"
                    binding.tvSunset.text = "${time(sunSet)}"
                    binding.tvSunrise.text = "${time(sunRise)}"
                    binding.tvWindSpeed.text = "$windSpeed"
                    binding.tvSeaLevel.text = "$seaLevel"
                    binding.tvMinTemp.text = "Min: $minTemp °C"
                    binding.tvMaxTemp.text = "Max: $maxTemp °C"

                    binding.tvDay.text = dayName(System.currentTimeMillis())
                    binding.tvDate.text = date()
                    binding.tvCityName.text="$cityName"
//                    Log.d("MAIN", "onResponse: $temperature")

//                    val currentTime = Instant.now().epochSecond
//                    Log.d("MAIN", "onResponse: $currentTime")
//                    val isDay = sunRise != null && sunSet != null && currentTime in sunRise..sunSet
//
//                    if (isDay) {
//                        changeImagesAccordingToWeatherCondition(condition, "day")
//                    } else {
//                        changeImagesAccordingToWeatherCondition(condition, "night")
//                    }

                    changeImagesAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })

    }


    private fun changeImagesAccordingToWeatherCondition(conditions:String) {
        when (conditions){
            "Clear Sky", "Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Smoke","Haze", "Mist", "Foggy", "Overcast", "Partly Cloudy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle","Moderate Rain", "Showers", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow","Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.night_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long?):String {
        val sdf = SimpleDateFormat("HH::mm", Locale.getDefault())
        if (timestamp!= null){
            return sdf.format((Date(timestamp*1000)))
        }
        return "j"
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}