package com.jokerweather.android.logic.model

/**
 * 空气质量实况
 */
data class AirNowResponse(val code:String,val now:Now){
    /**
     * aqi 空气质量指数
     */
    data class Now(val aqi:String)
}