package com.jokerweather.android.ui.place

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jokerweather.android.databinding.PlaceItemBinding
import com.jokerweather.android.logic.model.Location


/**
 * RecyclerView适配器
 */
class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Location>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val placeName: TextView = binding.placeName
        val placeAddress: TextView = binding.placeAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context))
        //binding root 返回所关联的布局
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.adm1
    }
    override fun getItemCount() = placeList.size
}