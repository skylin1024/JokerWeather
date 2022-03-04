package com.jokerweather.android.logic.model

data class Weather(
    val now: NowResponse.Now, val airNow: AirNowResponse.Now,
    val indicesDaily: List<IndicesNowResponse.Daily>,
    val daily: List<DailyResponse.Daily>
)