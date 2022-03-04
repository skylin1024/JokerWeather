package com.jokerweather.android.logic.model

import java.util.*

/**
 * 未来7天天气信息
 */
data class DailyResponse(val code:String,val daily:List<Daily>){
    /**
     *fxDate 预报日期
     *tempMax 预报当天最高温度
     *tempMin 预报当天最低温度
     *textDay 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
     *textNight 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
     */
    data class Daily(val fxDate:Date,val tempMax:Int,val tempMin:Int,
                     val textDay:String,val textNight:String)
}