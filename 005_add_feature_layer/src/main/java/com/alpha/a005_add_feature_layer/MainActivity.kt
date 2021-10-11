package com.alpha.a005_add_feature_layer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpha.a005_add_feature_layer.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem

// Ref: https://developers.arcgis.com/android/layers/tutorials/add-a-feature-layer/

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

        val map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)

        // create the service feature table
        val serviceFeatureTable = ServiceFeatureTable("https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads/FeatureServer/0")

        // create the feature layer using the service feature table
        val featureLayer = FeatureLayer(serviceFeatureTable)
        map.operationalLayers.add(featureLayer)

        mapview.map = map

        mapview.setViewpoint(Viewpoint(34.0270, -118.8050, 200000.0))
    }

}