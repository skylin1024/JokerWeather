package com.jokerweather.android.logic.model

import java.util.*

/**
 *当天各类生活指数
 */
data class IndicesNowResponse(val code:String,val daily:List<Daily>){
    /**
     * date 预报日期
     * type 生活指数类型ID 9表示感冒指数，2表示洗车指数，5表示紫外线指数，3表示穿衣指数。
     * name 生活指数类型的名称
     * category 生活指数预报级别名称
     */
    data class Daily(val date: Date,val type:String,val name: String,val category:String)
}