package com.alpha.a011_change_basemaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.core.view.get
import com.alpha.a011_change_basemaps.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView

// Ref: https://developers.arcgis.com/android/kotlin/sample-code/change-basemaps/

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mapView: MapView by lazy { binding.mapView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupMap()

        binding.lvBaseMaps.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, BasemapStyle.values().map { it.name.replace("_", " ") })

        binding.lvBaseMaps.setOnItemClickListener { _, _, position, _ ->
            mapView.map.basemap = Basemap(BasemapStyle.values()[position])
        }
    }

    private fun setupMap() {
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)
        mapView.map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)
        mapView.map.initialViewpoint = Viewpoint(30.052697, 31.198192, 72000.0)
    }


    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.dispose()
    }
}