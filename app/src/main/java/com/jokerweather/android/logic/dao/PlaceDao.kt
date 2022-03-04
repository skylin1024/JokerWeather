package com.jokerweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.jokerweather.android.JokerWeatherApplication
import com.jokerweather.android.logic.model.Location


object PlaceDao {
    /**
     * savePlace()方法用于将Place对象存储到SharedPreferences文件中，
     *这里使用了一个技巧，我们先通过GSON将Place对象转成一个JSON字符串，然后就可以用字符串存储的方式来保存数据了。
     */
    fun savePlace(place:Location){
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    /**
     * 在getSavedPlace()方法中，我们先将JSON字符串从SharedPreferences文件中读取出来，
     * 然后再通过GSON将JSON字符串解析成Place对象并返回。
     */
    fun getSavedPlace():Location{
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Location::class.java)
    }

    /**
     * 这里还提供了一个isPlaceSaved()方法，用于判断是否有数据已被存储。
     */
    fun isPlaceSaved() = sharedPreferences().contains("place")


    private fun sharedPreferences() = JokerWeatherApplication.context.
        getSharedPreferences("joker_weather", Context.MODE_PRIVATE)
}