package com.alpha.a013_manage_operational_layers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpha.a013_manage_operational_layers.databinding.LayerItemBinding
import com.esri.arcgisruntime.layers.Layer
import com.esri.arcgisruntime.mapping.LayerList

class ActiveListAdapter(private val dataSet: LayerList) :
    RecyclerView.Adapter<ActiveListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(dataSet[position])

    inner class ViewHolder(private val binding: LayerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(layer: Layer){
            binding.layer = layer
        }
    }
}