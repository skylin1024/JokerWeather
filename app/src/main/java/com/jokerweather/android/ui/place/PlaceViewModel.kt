package com.jokerweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.jokerweather.android.logic.Repository
import com.jokerweather.android.logic.model.Location


class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()
    //我们还在PlaceViewModel中定义了一个placeList集合，用于对界面上显示的城市数据进行缓存
    val placeList = ArrayList<Location>()
    //使用Transformations的switchMap()方法来观察searchLiveData这个对象
    val placeLiveData = Transformations.switchMap(searchLiveData){location ->
        Repository.searchPlaces(location)
    }

    fun searchPlaces(location: String){
        searchLiveData.value = location
    }
}