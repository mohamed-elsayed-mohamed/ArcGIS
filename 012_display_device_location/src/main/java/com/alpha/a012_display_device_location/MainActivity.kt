package com.alpha.a012_display_device_location

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alpha.a012_display_device_location.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.mapping.view.MapView

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mapView: MapView by lazy { binding.mapView }
    private val locationDisplay: LocationDisplay by lazy { mapView.locationDisplay }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()
    }

    private fun setupMap() {
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)
        mapView.map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)

        locationDisplay.addDataSourceStatusChangedListener {
            if(!it.isStarted && it.error != null){
                requestPermission()
            }
        }

        val actionList = arrayOf("Stop", "On", "Re-Center", "Navigation", "Compass")

        binding.spinner.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, actionList)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when(position){
                    0 -> if(locationDisplay.isStarted) locationDisplay.stop()
                    1 -> if(!locationDisplay.isStarted) locationDisplay.startAsync()
                    2 -> {
                        locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.RECENTER
                        if(!locationDisplay.isStarted) locationDisplay.startAsync()
                    }
                    3 -> {
                        locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION
                        if(!locationDisplay.isStarted) locationDisplay.startAsync()
                    }
                    4 -> {
                        locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.COMPASS_NAVIGATION
                        if(!locationDisplay.isStarted) locationDisplay.startAsync()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun requestPermission() {
        val requestCode = 2
        val requestPermissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        val checkFineLocation = ContextCompat.checkSelfPermission(this, requestPermissions.first()) == PackageManager.PERMISSION_GRANTED
        val checkCoarseLocation = ContextCompat.checkSelfPermission(this, requestPermissions.last()) == PackageManager.PERMISSION_GRANTED

        if(!(checkFineLocation && checkCoarseLocation)){
            ActivityCompat.requestPermissions(this, requestPermissions, requestCode)
        } else {
            Toast.makeText(this, "Can't access location!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED){
            locationDisplay.startAsync()
        } else{
            Toast.makeText(this, "Please active you location!", Toast.LENGTH_SHORT).show()
        }
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