package com.cicklum.theweather.views

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cicklum.theweather.API_KEY
import com.cicklum.theweather.BASE_URL
import com.cicklum.theweather.R
import com.cicklum.theweather.network.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    var url_weather: String = ""
    var city: String = ""
    var cityList: ArrayList<CityData> = ArrayList()

    companion object {
        const val earthRadiusKm: Double = 6372.8
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        btn_buscar.setOnClickListener {
            city = edit_buscar.text.toString()
            getWeather(city)
            edit_buscar.setText("")
            cityList = ArrayList()
        }
    }

    fun getHighestTemperature(list: ArrayList<CityData>) {
        var temperature = list.get(0).temperature
        var highest:Int
        for (weather in list) {
            if (weather.temperature >= temperature) {
                highest = weather.temperature
                highestTemperature.text = "Temperatura más alta en:${weather.cityName}, $highest ºC"
            }
        }
    }

    fun getHighestHumidity(list: ArrayList<CityData>) {
        var humidity = list.get(0).humidity
        var highest: Int
        for (weather in list) {
            if (weather.humidity >= humidity) {
                highest = weather.humidity
                highestHumidity.text = "Humedad más alta en:${weather.cityName}, $highest %"
            }
        }
    }

    fun getHighestRained(list: ArrayList<CityData>) {
        var rain = list.get(0).rain
        var highest: Double
        for (weather in list) {
            if (weather.rain >= rain) {
                highest = weather.rain
                highestRain.text = "Más lluvia en:${weather.cityName}, $highest"
            }
        }
    }

    fun getHighestWindSpeed(list: ArrayList<CityData>) {
        var windy = list.get(0).windy
        var highest: Double
        for (weather in list) {
            if (weather.windy > windy) {
                highest = weather.windy
                highestWindSpeed.text = "Velocidad más alta en:${weather.cityName}, $highest"
            }
        }
    }

    private fun getWeather(city: String) {
        url_weather = BASE_URL + "weather?q=" + city + "&appid=" + API_KEY
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url_weather,
            Response.Listener<String> {
                val responseJson = JSONObject(it)
                val coordinates = getCoordinateFromJson(responseJson)
                val main = getMainFromJson(responseJson)
                val windy = getWindyFromJson(responseJson)
                val temperatureFormatted = kelvinToCelsius(main.temp)
                val rain = getRainFromJson(responseJson)
                val df = DecimalFormat("#.##")
                cityList.add(
                    CityData(
                        city,
                        Integer.parseInt(df.format(temperatureFormatted)),
                        main.humidity,
                        rain.rain1h,
                        windy.speed
                    )
                )
                haversineNorth(coordinates.lat, coordinates.long)
                haversineSouth(coordinates.lat, coordinates.long)
                haversineEast(coordinates.lat, coordinates.long)
                haversineWest(coordinates.lat, coordinates.long)
            },
            Response.ErrorListener {
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun haversineNorth(lat: Double, long: Double): Coordinadas {
        val lat1 = Math.toRadians(lat)
        val lon1 = Math.toRadians(long)

        var lat2 = Math.asin(
            Math.sin(lat1) * Math.cos(200 / earthRadiusKm) +
                    Math.cos(lat1) * Math.sin(200 / earthRadiusKm) * Math.cos(1.57)
        )
        var lon2 = lon1 + Math.atan2(
            Math.sin(1.57) * Math.sin(200 / earthRadiusKm) * Math.cos(lat1),
            Math.cos(200 / earthRadiusKm) - Math.sin(lat1) * Math.sin(lat2)
        )

        lat2 = Math.toDegrees(lat2)
        lon2 = Math.toDegrees(lon2)

        var coord = Coordinadas(lat2, lon2)
        getCityByCoordinates(lat2, lon2)

        return coord
    }

    fun haversineSouth(lat: Double, long: Double): Coordinadas {
        val lat1 = Math.toRadians(lat)
        val lon1 = Math.toRadians(long)

        var south = Math.toRadians(3.14)

        var lat2 = Math.asin(
            Math.sin(lat1) * Math.cos(200 / earthRadiusKm) +
                    Math.cos(lat1) * Math.sin(200 / earthRadiusKm) * Math.cos(south)
        )
        var lon2 = lon1 + Math.atan2(
            Math.sin(south) * Math.sin(200 / earthRadiusKm) * Math.cos(lat1),
            Math.cos(200 / earthRadiusKm) - Math.sin(lat1) * Math.sin(lat2)
        )

        lat2 = Math.toDegrees(lat2)
        lon2 = Math.toDegrees(lon2)

        var coord = Coordinadas(lat2, lon2)
        getCityByCoordinates(lat2, lon2)

        return coord
    }

    fun haversineEast(lat: Double, long: Double): Coordinadas {
        val lat1 = Math.toRadians(lat)
        val lon1 = Math.toRadians(long)

        var east = Math.toRadians(180.0)

        var lat2 = Math.asin(
            Math.sin(lat1) * Math.cos(200 / earthRadiusKm) +
                    Math.cos(lat1) * Math.sin(200 / earthRadiusKm) * Math.cos(east)
        )
        var lon2 = lon1 + Math.atan2(
            Math.sin(east) * Math.sin(200 / earthRadiusKm) * Math.cos(lat1),
            Math.cos(200 / earthRadiusKm) - Math.sin(lat1) * Math.sin(lat2)
        )

        lat2 = Math.toDegrees(lat2)
        lon2 = Math.toDegrees(lon2)

        var coord = Coordinadas(lat2, lon2)
        getCityByCoordinates(lat2, lon2)

        return coord
    }

    fun haversineWest(lat: Double, long: Double): Coordinadas {
        val lat1 = Math.toRadians(lat)
        val lon1 = Math.toRadians(long)

        var south = Math.toRadians(-90.0)

        var lat2 = Math.asin(
            Math.sin(lat1) * Math.cos(200 / earthRadiusKm) +
                    Math.cos(lat1) * Math.sin(200 / earthRadiusKm) * Math.cos(south)
        )
        var lon2 = lon1 + Math.atan2(
            Math.sin(south) * Math.sin(200 / earthRadiusKm) * Math.cos(lat1),
            Math.cos(200 / earthRadiusKm) - Math.sin(lat1) * Math.sin(lat2)
        )

        lat2 = Math.toDegrees(lat2)
        lon2 = Math.toDegrees(lon2)

        var coord = Coordinadas(lat2, lon2)
        getCityByCoordinates(lat2, lon2)

        return coord
    }

    private fun getCityByCoordinates(lat: Double, long: Double) {
        url_weather = BASE_URL + "weather?lat=$lat&lon=$long&appid=" + API_KEY
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url_weather,
            Response.Listener<String> {
                val responseJson = JSONObject(it)
                val city = responseJson.getString(name)
                val main = getMainFromJson(responseJson)
                val windy = getWindyFromJson(responseJson)
                val rain = getRainFromJson(responseJson)
                val temperatureFormatted = kelvinToCelsius(main.temp)
                val df = DecimalFormat("#.##")
                cityList.add(
                    CityData(
                        city,
                        Integer.parseInt(df.format((temperatureFormatted))),
                        main.humidity,
                        rain.rain1h,
                        windy.speed
                    )
                )
                runOnUiThread({
                    var weatherAdapter = WeatherAdapter(cityList)
                    list_citys.adapter = weatherAdapter
                    list_citys.deferNotifyDataSetChanged()
                    getHighestTemperature(cityList)
                    getHighestHumidity(cityList)
                    getHighestRained(cityList)
                    getHighestWindSpeed(cityList)
                })
            },
            Response.ErrorListener {
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun kelvinToCelsius(kelvinTemp: Double): Int {
        return kelvinTemp.toInt() - 273
    }

}
