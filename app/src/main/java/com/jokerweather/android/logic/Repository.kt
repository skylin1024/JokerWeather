package com.jokerweather.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.jokerweather.android.logic.network.JokerWeatherNetwork
import kotlinx.coroutines.Dispatchers

/**
 * 仓库层的统一封装入口
 */
object Repository {
    /**
     * liveData()函数自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文
     * liveData()函数的线程参数类型指定成了Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中了。
     */
    fun searchPlaces(location:String) = liveData(Dispatchers.IO){
        val result = try {
            val placeResponse = JokerWeatherNetwork.searchPlaces(location)
            if (placeResponse.code == "200"){
                Log.d("Repository","success")
                val place = placeResponse.location
                Result.success(place)
            }else{
                Log.d("Repository","failure")
                Result.failure(RuntimeException("response status is ${placeResponse.code}"))
            }
        }catch (e:Exception){
            Result.failure(e)
        }
        //通知数据变化
        emit(result)
    }
}