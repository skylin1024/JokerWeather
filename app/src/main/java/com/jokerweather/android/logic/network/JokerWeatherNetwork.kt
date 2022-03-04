package com.jokerweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 统一的网络数据源访问入口
 */
object JokerWeatherNetwork {
    //动态代理对象
    private val placeService = PlaceServiceCreator.create<PlaceService>()
    private val weatherService = WeatherServiceCreator.create<WeatherService>()

    //地点
    suspend fun searchPlaces(location: String) = placeService.searchPlaces(location).await()
    //经纬度
    suspend fun getNowWeather(location: String) = weatherService.getNowWeather(location).await()
    suspend fun getAirNowWeather(location: String) = weatherService.getAirNowWeather(location).await()
    suspend fun getIndicesNowWeather(location: String) = weatherService.getIndicesNowWeather(location).await()
    suspend fun getDailyWeather(location: String) = weatherService.getDailyWeather(location).await()

    /**
     * suspend关键字，使用它可以将任意函数声明成挂起函数，而挂起函数之间都是可以互相调用的
     * suspendCoroutine函数必须在协程作用域或挂起函数中才能调用，它接收一个Lambda表达式参数，
     * 主要作用是将当前协程立即挂起，然后在一个普通的线程中执行Lambda表达式中的代码。
     */
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null){
                        continuation.resume(body)
                    }else{
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}