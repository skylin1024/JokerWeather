package com.jokerweather.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.jokerweather.android.logic.dao.PlaceDao
import com.jokerweather.android.logic.model.Location
import com.jokerweather.android.logic.model.Weather
import com.jokerweather.android.logic.network.JokerWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * 仓库层的统一封装入口
 */
object Repository {
    //对Dao层的代码进行封装，
    //最佳的实现方式肯定还是开启一个线程来执行这些比较耗时的任务，
    //然后通过LiveData对象进行数据返回
    fun savePlace(place: Location) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
    /**
     * liveData()函数自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文
     * liveData()函数的线程参数类型指定成了Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中了。
     */
    fun searchPlaces(location: String) = fire(Dispatchers.IO) {
        val placeResponse = JokerWeatherNetwork.searchPlaces(location)
        if (placeResponse.code == "200") {

            val place = placeResponse.location
            Result.success(place)
        } else {

            Result.failure(RuntimeException("response status is ${placeResponse.code}"))
        }
    }

    /**
     * 刷新天气信息
     */
    fun refreshWeather(location:String) = fire(Dispatchers.IO) {
        coroutineScope {
            //并发执行请求
            val deferredNow = async {
                JokerWeatherNetwork.getNowWeather(location)
            }
            val deferredAirNow = async {
                JokerWeatherNetwork.getAirNowWeather(location)
            }
            val deferredIndicesNow = async {
                JokerWeatherNetwork.getIndicesNowWeather(location)
            }
            val deferredDaily = async {
                JokerWeatherNetwork.getDailyWeather(location)
            }
            val nowResponse = deferredNow.await()
            val airNowResponse = deferredAirNow.await()
            val indicesNowResponse = deferredIndicesNow.await()
            val dailyResponse = deferredDaily.await()

            if (nowResponse.code == "200" && airNowResponse.code == "200" && indicesNowResponse.code == "200" && dailyResponse.code == "200") {
                val weather = Weather(
                    nowResponse.now,
                    airNowResponse.now,
                    indicesNowResponse.daily,
                    dailyResponse.daily
                )
                Log.d("Repository", "success")
                Result.success(weather)
            } else {
                Log.d("Repository", "failure")
                Result.failure(
                    RuntimeException(
                        "now Response code is ${nowResponse.code}" + "airNow Response code is ${airNowResponse.code}"
                                + "indicesNow Response code is ${indicesNowResponse.code}" + "daily Response code is ${dailyResponse.code}"
                    )
                )
            }
        }

    }

    /**
     * 在fire()函数的内部会先调用一下liveData()函数，
     * 然后在liveData()函数的代码块中统一进行了try catch处理，并在try语句中调用传入的Lambda表达式中的代码，
     * 最终获取Lambda表达式的执行结果并调用emit()方法发射出去。
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure(e)
            }
            //通知数据变化
            emit(result)
        }
}