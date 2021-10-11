package com.alpha.a003_display_web_map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpha.a003_display_web_map.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem

// Ref: https://developers.arcgis.com/android/maps-2d/tutorials/display-a-web-map/

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mapview: MapView by lazy { binding.map }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()
    }

    private fun setupMap(){
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        val portal = Portal("https://www.arcgis.com")

        // USA Electric Fuel Stations
        // https://www.arcgis.com/home/item.html?id=e32fe7d88f6b4be5a4013142ec807e66

        val itemId = "e32fe7d88f6b4be5a4013142ec807e66"
        val portalItem = PortalItem(portal, itemId)
        val map = ArcGISMap(portalItem)

        mapview.map = map
    }
}