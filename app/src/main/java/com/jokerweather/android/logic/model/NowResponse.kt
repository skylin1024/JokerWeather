package com.jokerweather.android.logic.model

/**
 * 实况天气
 * 注意，这里我们将所有的数据模型类都定义在了NowResponse的内部，这样可以防止出现和其他接口的数据模型类有同名冲突的情况。
 */
data class NowResponse(val code:String,val now:Now){
    /**
     * temp表示当前的温度，text表示当前的天气情况
     */
    data class Now(val temp:String,val text:String)
}