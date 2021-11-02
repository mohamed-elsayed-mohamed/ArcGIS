package com.alpha.a007_search_for_address

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alpha.a007_search_for_address.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.TextSymbol
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult
import com.esri.arcgisruntime.tasks.geocode.LocatorTask

// Ref: https://developers.arcgis.com/android/geocode-and-search/tutorials/search-for-an-address/

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val graphicsOverlay: GraphicsOverlay by lazy { GraphicsOverlay() }

    private val locatorTask = LocatorTask("https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()

        setupSearchViewListener()
    }

    private fun setupMap(){
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        val arcGISMap = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)

        binding.mapView.apply {
            map = arcGISMap
            this.setViewpoint(Viewpoint(34.0270, -118.8050, 200000.0))
            this.graphicsOverlays.add(graphicsOverlay)
        }
    }

    private fun setupSearchViewListener() {
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(nextText: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                performGeocode(query)
                return false
            }

        })
    }

    private fun performGeocode(query: String) {

        val geocodeParameters = GeocodeParameters().apply {
            resultAttributeNames.add("*")
            maxResults = 1
            outputSpatialReference = binding.mapView.spatialReference
        }

        val geocodeResultFuture = locatorTask.geocodeAsync(query, geocodeParameters)

        geocodeResultFuture.addDoneListener {
            try {
                val geocodeResult = geocodeResultFuture.get()
                if (geocodeResult.isNotEmpty()) {
                    displayResult(geocodeResult[0])
                } else {
                    Toast.makeText(this, "No results found.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(MainActivity::class.simpleName, "Error getting result" + e.message)
            }
        }
    }

    private fun displayResult(geocodeResult: GeocodeResult) {
        // clear the overlay of any previous result
        graphicsOverlay.graphics.clear()

        // create a graphic to display the address text
        val textSymbol = TextSymbol(
            18f,
            geocodeResult.label,
            Color.BLACK,
            TextSymbol.HorizontalAlignment.CENTER,
            TextSymbol.VerticalAlignment.BOTTOM
        )

        val textGraphic = Graphic(geocodeResult.displayLocation, textSymbol)
        graphicsOverlay.graphics.add(textGraphic)

        binding.mapView.setViewpointCenterAsync(geocodeResult.displayLocation)
    }

    override fun onPause() {
        binding.mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onDestroy() {
        binding.mapView.dispose()
        super.onDestroy()
    }
}