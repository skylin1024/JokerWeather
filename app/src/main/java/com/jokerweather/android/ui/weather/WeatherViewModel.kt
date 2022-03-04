package com.jokerweather.android.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.jokerweather.android.logic.Repository
import com.jokerweather.android.logic.model.Location

class WeatherViewModel:ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()
    //以下三个变量都是和界面相关的数据，
    //放到ViewModel中可以保证它们在手机屏幕发生旋转的时候不会丢失，稍后在编写UI层代码的时候会用到这几个变量。
    var locationLon = ""
    var locationLat = ""
    var placeName = ""

    //经纬度需保留两位小数
    val weatherLiveData = Transformations.switchMap(locationLiveData){location ->
        val lon = String.format("%.2f",location.lon.toDouble())
        val lat = String.format("%.2f",location.lat.toDouble())
        Repository.refreshWeather("$lon,$lat")
    }

    fun refreshWeather(lon:String,lat:String){
        locationLiveData.value = Location("",lon,lat,"")
    }
}