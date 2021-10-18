package com.alpha.a006_display_symbols

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpha.a006_display_symbols.databinding.ActivityMainBinding
import com.alpha.a006_display_symbols.databinding.StyleControlsLayoutBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.symbology.DictionaryRenderer
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle

// Ref: https://developers.arcgis.com/android/styles-and-data-visualization/display-symbols-with-a-dictionary-renderer/
/*
    Download the data from ArcGIS Online.
    Extract the contents of the downloaded zip file to disk.
    Open your command prompt and navigate to the folder where you extracted the contents of the data from step 1.
    Execute the following command: adb push Restaurant.stylx /Android/data/com.esri.arcgisruntime.sample.customdictionarystyle/files/Restaurant.stylx
 */

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val includeBinding: StyleControlsLayoutBinding by lazy { StyleControlsLayoutBinding.inflate(layoutInflater) }
    private val mapView: MapView by lazy { binding.map }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()

        val featureTable = ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/Redlands_Restaurants/FeatureServer/0")
        val featureLayer = FeatureLayer(featureTable)

        // add the the feature layer to the map's operational layers
        mapView.map.operationalLayers.add(featureLayer)
        mapView.map.initialViewpoint = Viewpoint(34.0574, -117.1963, 5000.0)

        // create a dictionary symbol style from the stylx file
        val dictionarySymbolStyleFromFile =
            DictionarySymbolStyle.createFromFile(getExternalFilesDir(null)?.path + "/Restaurant.stylx")
        // create a new dictionary renderer from the dictionary symbol style
        val dictionaryRendererFromFile = DictionaryRenderer(dictionarySymbolStyleFromFile)

        // on style file click
        includeBinding.styleFileRadioButton.setOnClickListener {
            // set the feature layer renderer to the dictionary renderer from local stylx file
            featureLayer.renderer = dictionaryRendererFromFile
        }
        // set the initial state to use the dictionary renderer from local stylx file
        includeBinding.styleFileRadioButton.performClick()

        // create a portal item using the portal and the item id of the dictionary web style
        val portal = Portal("https://arcgisruntime.maps.arcgis.com")
        val portalItem = PortalItem(portal, "adee951477014ec68d7cf0ea0579c800")
        // map the input fields in the feature layer to the dictionary symbol style's expected fields for symbols and text
        val fieldMap: HashMap<String, String> = HashMap()
        fieldMap["healthgrade"] = "Inspection"
        // create a new dictionary symbol style from the web style in the portal item
        val dictionarySymbolStyleFromPortal = DictionarySymbolStyle(portalItem)
        // create a new dictionary renderer from the dictionary symbol style
        val dictionaryRendererFromPortal = DictionaryRenderer(dictionarySymbolStyleFromPortal, fieldMap, HashMap())

        // on web style click
        includeBinding.webStyleRadioButton.setOnClickListener {
            // set the feature layer renderer to the dictionary renderer from portal
            featureLayer.renderer = dictionaryRendererFromPortal
        }

    }

    private fun setupMap(){
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        mapView.map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)
    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }
}