package com.alpha.a013_manage_operational_layers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpha.a013_manage_operational_layers.databinding.InactiveLayerItemBinding
import com.esri.arcgisruntime.layers.Layer

class InactiveListAdapter(
    private val dataSet: MutableList<Layer>,
    private val onItemClick: (pos: Int) -> Unit
) : RecyclerView.Adapter<InactiveListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            InactiveLayerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(dataSet[position], onItemClick)

    inner class ViewHolder(private val binding: InactiveLayerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(layer: Layer, onClick: ((pos: Int) -> Unit)) {
            binding.layer = layer
            binding.plus.setOnClickListener { onClick.invoke(adapterPosition) }
        }
    }
}