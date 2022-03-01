package com.jokerweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
/**
 * 全局获取Context
 */
class JokerWeatherApplication: Application() {
    companion object{
        //和风天气申请到的KEY
        const val KEY = "a88601d456e1463a94da28bef2077cc5"
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}