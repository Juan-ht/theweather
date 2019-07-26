package com.cicklum.theweather.views

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cicklum.theweather.R
import com.cicklum.theweather.inflate
import com.cicklum.theweather.network.CityData

class WeatherAdapter(val dataSource: ArrayList<CityData>) : BaseAdapter() {
    override fun getView(position: Int, currentView: View?, parentView: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (currentView == null) {
            view = parentView.inflate(R.layout.item_weather)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = currentView.tag as ViewHolder
            view = currentView
        }
        val weather = dataSource[position]
        viewHolder.apply {
            city.text = weather.cityName
            temperature.text = "Temperatura: ${weather.temperature}  ºC"
            rain.text = "Ha llovido: ${weather.rain}"
            humidity.text = "Humedad: ${weather.humidity} %"
            wind.text = "Velocidad del viento: ${weather.windy}"
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    private class ViewHolder(view: View) {
        val city: TextView = view.findViewById(R.id.cityName)
        val temperature: TextView = view.findViewById(R.id.temperature)
        val rain: TextView = view.findViewById(R.id.rainText)
        val humidity: TextView = view.findViewById(R.id.humidityText)
        val wind: TextView = view.findViewById(R.id.windy)

    }
}