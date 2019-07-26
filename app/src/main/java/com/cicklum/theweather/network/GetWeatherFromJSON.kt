package com.cicklum.theweather.network

import org.json.JSONObject

fun getCoordinateFromJson(response: JSONObject): Coordinadas {

    val currentJson = response.getJSONObject(coord)

    with(currentJson) {
        val coordinadas = Coordinadas(getDouble(lat), getDouble(lon)
            )
        return coordinadas
    }
}

fun getMainFromJson(response: JSONObject): Main {
    val currentJson = response.getJSONObject(main)

    with(currentJson) {
        val main = Main(getInt(humidity),
            getDouble(temp))
        return main
    }

}

fun getRainFromJson(response: JSONObject): Rain {

    if(response.has(rain)){
        val currentJson = response.getJSONObject(rain)
        with(currentJson) {
            val main = Rain(getDouble(rain))
            return main
        }
    }else{
        val rain = Rain(0.0)
        return rain
    }

}

fun getWindyFromJson(response: JSONObject): Windy {
    val currentJson = response.getJSONObject(wind)

    with(currentJson) {
        val main = Windy(getDouble(speed))
        return main
    }

}