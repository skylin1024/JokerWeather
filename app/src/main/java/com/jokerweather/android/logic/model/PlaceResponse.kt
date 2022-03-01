package com.jokerweather.android.logic.model

/**
 * code表示API状态码
 */
data class PlaceResponse(val code:String,val location:List<Location>)

/**
 * name 城市名称
 * lon 经度
 * lat 纬度
 * adm1 该地区的所属一级行政区域
 */

data class Location(val name:String,val lon:String,val lat:String, val adm1:String)
