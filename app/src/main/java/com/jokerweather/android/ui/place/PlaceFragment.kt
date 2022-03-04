package com.jokerweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jokerweather.android.MainActivity
import com.jokerweather.android.databinding.FragmentPlaceBinding
import com.jokerweather.android.logic.network.WeatherServiceCreator
import com.jokerweather.android.ui.weather.WeatherActivity

class PlaceFragment:Fragment(){
    //lazy函数这种懒加载技术来获取PlaceViewModel的实例
    val viewModel by lazy {
        ViewModelProvider(this)[PlaceViewModel::class.java]
    }
    private lateinit var adapter: PlaceAdapter
    private var _binding: FragmentPlaceBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    //!!表示该表达式非空
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        如果当前已有存储的城市数据，那么就获取已存储的数
//        据并解析成Place对象，然后使用它的经纬度坐标和城市名直接跳转并传递给
//        WeatherActivity，这样用户就不需要每次都重新搜索并选择城市了。
        //只有当PlaceFragment被嵌入MainActivity中，
        //并且之前已经存在选中的城市，此时才会直接跳转到WeatherActivity
        if(viewModel.isPlaceSaved() && activity is MainActivity){
            val place = viewModel.getSavedPlace()
            val intent = Intent(context,WeatherActivity::class.java).apply {
                putExtra("location_lon", place.lon)
                putExtra("location_lat", place.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this,viewModel.placeList)
        binding.recyclerView.adapter = adapter

        binding.searchPlaceEdit.addTextChangedListener{editable ->
            val context = editable.toString()
            if (context.isNotEmpty()){
                //搜索城市数据
                viewModel.searchPlaces(context)
            }else{
                //而当输入搜索框中的内容为空时，我们就将RecyclerView隐藏起来，同时将那张仅用于美观用途的背景图显示出来。
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(this,{result ->
            val places = result.getOrNull()
            if (places != null){
                binding.recyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}