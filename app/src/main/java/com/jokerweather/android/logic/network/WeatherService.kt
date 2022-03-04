package com.jokerweather.android.logic.network

import com.jokerweather.android.JokerWeatherApplication
import com.jokerweather.android.logic.model.AirNowResponse
import com.jokerweather.android.logic.model.DailyResponse
import com.jokerweather.android.logic.model.IndicesNowResponse
import com.jokerweather.android.logic.model.NowResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * lon 经度
 * lat 纬度
 */
interface WeatherService {
    @GET("v7/weather/now?key=${JokerWeatherApplication.KEY}")
    fun getNowWeather(@Query("location")location:String):Call<NowResponse>

    @GET("v7/air/now?key=${JokerWeatherApplication.KEY}")
    fun getAirNowWeather(@Query("location")location:String):Call<AirNowResponse>

    @GET("v7/weather/7d?key=${JokerWeatherApplication.KEY}")
    fun getDailyWeather(@Query("location")location:String):Call<DailyResponse>

    //type 生活指数类型ID 9表示感冒指数，2表示洗车指数，5表示紫外线指数，3表示穿衣指数。
    @GET("v7/indices/1d?type=2,3,5,9&key=${JokerWeatherApplication.KEY}")
    fun getIndicesNowWeather(@Query("location")location:String):Call<IndicesNowResponse>
}