package com.jokerweather.android.ui.weather


import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.jokerweather.android.R
import com.jokerweather.android.databinding.ActivityWeatherBinding
import com.jokerweather.android.logic.model.Weather
import com.jokerweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }
    lateinit var binding: ActivityWeatherBinding

    companion object {
        const val DAYS = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Android 11以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //隐藏状态栏
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            window.statusBarColor = Color.TRANSPARENT
        } else {
            //我们调用了getWindow().getDecorView()方法拿到当前Activity的DecorView，
            //再调用它的setSystemUiVisibility()方法来改变系统UI的显示，这里传入
            //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN和View.SYSTEM_UI_FLAG_LAYOUT_STABLE就表示Activity的布局会显示在状态栏上面，
            //最后调用一下setStatusBarColor()方法将状态栏设置成透明色。
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        //接收经纬度及地名数据
        if (viewModel.locationLon.isEmpty()) {
            viewModel.locationLon = intent.getStringExtra("location_lon") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        //第一，在切换城市按钮的点击事件中调用DrawerLayout的openDrawer()方法来打开滑动菜单；
        //第二，监听DrawerLayout的状态，当滑动菜单被隐藏的时候，同时也要隐藏输入法。
        binding.now.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })

        viewModel.weatherLiveData.observe(this, { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            //刷新事件结束隐藏进度条
            binding.swipeRefresh.isRefreshing = false
        })
        binding.swipeRefresh.setColorSchemeResources(R.color.purple_500)
        refreshWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

    }

    private fun showWeatherInfo(weather: Weather) {
        binding.now.placeName.text = viewModel.placeName
        val now = weather.now
        val daily = weather.daily
        val airNow = weather.airNow
        val indicesNow = weather.indicesDaily
        // 填充now.xml布局中的数据
        val currentTempText = "${now.temp.toInt()} ℃"
        binding.now.currentTemp.text = currentTempText
        binding.now.currentSky.text = getSky(now.text).info
        val currentPM25Text = "空气指数 ${airNow.aqi.toInt()}"
        binding.now.currentAQI.text = currentPM25Text
        binding.now.nowLayout.setBackgroundResource(getSky(now.text).bg)
        // 填充forecast.xml布局中的数据
        binding.forecast.forecastLayout.removeAllViews()
        for (i in 0 until DAYS) {
            val fxDate = daily[i].fxDate
            val textDay = daily[i].textDay
            val tempMax = daily[i].tempMax
            val tempMin = daily[i].tempMin
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                binding.forecast.forecastLayout, false
            )
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(fxDate)
            val sky = getSky(textDay)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "$tempMin ~ $tempMax ℃"
            temperatureInfo.text = tempText
            binding.forecast.forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        //type 生活指数类型ID 9表示感冒指数，2表示洗车指数，5表示紫外线指数，3表示穿衣指数。
        binding.lifeIndex.coldRiskText.text = indicesNow[3].category
        binding.lifeIndex.dressingText.text = indicesNow[2].category
        binding.lifeIndex.ultravioletText.text = indicesNow[1].category
        binding.lifeIndex.carWashingText.text = indicesNow[0].category
        binding.weatherLayout.visibility = View.VISIBLE
    }

    /**
     * 刷新天气
     */
    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLon, viewModel.locationLat)
        //显示下拉进度条
        binding.swipeRefresh.isRefreshing = true
    }

}