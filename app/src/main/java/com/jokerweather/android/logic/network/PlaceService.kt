package com.jokerweather.android.logic.network

import com.jokerweather.android.JokerWeatherApplication
import com.jokerweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/city/lookup?key=${JokerWeatherApplication.KEY}")
    fun searchPlaces(@Query("location")location: String):Call<PlaceResponse>
}