package com.jokerweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jokerweather.android.R
import com.jokerweather.android.databinding.PlaceItemBinding
import com.jokerweather.android.logic.model.Location
import com.jokerweather.android.logic.model.Weather
import com.jokerweather.android.ui.weather.WeatherActivity


/**
 * RecyclerView适配器
 */
class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Location>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val placeName: TextView = binding.placeName
        val placeAddress: TextView = binding.placeAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        //这里我们给place_item.xml的最外层布局注册了一个点击事件监听器，
        //然后在点击事件中获取当前点击项的经纬度坐标和地区名称，并把它们传入Intent中，
        //最后调用Fragment的startActivity()方法启动WeatherActivity。
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            val position = holder.absoluteAdapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            //如果是在WeatherActivity中，那么就关闭滑动菜单，给WeatherViewModel赋值新的经纬度坐标和地区名称，
            //然后刷新城市的天气信息；而如果是在MainActivity中，那么就保持之前的处理逻辑不变即可。
            if (activity is WeatherActivity){
                //关闭滑动窗口
                activity.binding.drawerLayout.closeDrawers()
                activity.viewModel.locationLon = place.lon
                activity.viewModel.locationLat = place.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            }else{
                val intent = Intent(parent.context,WeatherActivity::class.java).apply {
                    putExtra("location_lon", place.lon)
                    putExtra("location_lat", place.lat)
                    putExtra("place_name", place.name)
                }
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            //保存地点
            fragment.viewModel.savePlace(place)
        }
        //binding root 返回所关联的布局
        return holder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.adm1
    }
    override fun getItemCount() = placeList.size
}