package com.alpha.a009_route_and_directions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpha.a009_route_and_directions.databinding.ActivityMainBinding
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.tasks.networkanalysis.Stop

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val routeStops: MutableList<Stop> by lazy { mutableListOf() }

    private val graphicsOverlay: GraphicsOverlay by lazy { GraphicsOverlay() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setupMap()
    }
}