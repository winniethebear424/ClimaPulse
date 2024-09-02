package com.example.climapulse

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.climapulse.R.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val API: String = "8c5dbb5dd791aa79544faa40e106dd7a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val searchButton: Button = findViewById(id.searchButton)
        val citySearch: EditText = findViewById(id.citySearch)

        searchButton.setOnClickListener{
            val city = citySearch.text.toString()
            if (city.isNotEmpty()){
                weatherTask(city).execute()
            }
            else{
                citySearch.error = "Please enter a city name"
            }
        }
    }
    inner class weatherTask(private val city: String): AsyncTask<String, Void, String>(){
        override fun onPreExecute(){
            super.onPreExecute()

            //showing the progress bar, remove the main design
            findViewById<ProgressBar>(id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(id.mainContainer).visibility = View.GONE
            findViewById<TextView>(id.errorText).visibility = View.GONE
        }
        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API").
                readText(Charsets.UTF_8)
            }catch(e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).
                format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */
                findViewById<TextView>(id.updated_at).text =  updatedAtText
                findViewById<TextView>(id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(id.temp).text = temp
                findViewById<TextView>(id.temp_min).text = tempMin
                findViewById<TextView>(id.temp_max).text = tempMax
                findViewById<TextView>(id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).
                format(Date(sunrise*1000))
                findViewById<TextView>(id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).
                format(Date(sunset*1000))
                findViewById<TextView>(id.wind).text = windSpeed
                findViewById<TextView>(id.pressure).text = pressure
                findViewById<TextView>(id.humidity).text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(id.mainContainer).visibility = View.VISIBLE

                findViewById<TextView>(id.address).text = address

            } catch (e: Exception) {
                findViewById<ProgressBar>(id.loader).visibility = View.GONE
                findViewById<TextView>(id.errorText).visibility = View.VISIBLE
            }
        }
    }

}