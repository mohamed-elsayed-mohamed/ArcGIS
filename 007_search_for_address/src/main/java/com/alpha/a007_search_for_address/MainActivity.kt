package com.alpha.a007_search_for_address

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alpha.a007_search_for_address.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.MobileMapPackage
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import java.io.File

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mapview: MapView by lazy { binding.map }

    private var defaultViewPoint: Viewpoint = Viewpoint(30.052697, 31.198192, 72000.0)

    private lateinit var mapPackage: MobileMapPackage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()

        loadMobileMapPackage(getFileFromAssets("yellowstone.mmpk").absolutePath)
    }

    private fun getFileFromAssets(fileName: String): File =
        File(this.cacheDir, fileName)
            .also {
                it.outputStream()
                    .use { cache ->
                        this.assets.open(fileName)
                            .use { inputStream -> inputStream.copyTo(cache) }
                    }
            }

    private fun loadMobileMapPackage(mmpkFile: String) {
        // create the mobile map package
        mapPackage = MobileMapPackage(mmpkFile).also {
            // load the mobile map package asynchronously
            it.loadAsync()
        }

        // add done listener which will invoke when mobile map package has loaded
        mapPackage.addDoneLoadingListener() {
            // check load status and that the mobile map package has maps
            if (mapPackage.loadStatus === LoadStatus.LOADED && mapPackage.maps.isNotEmpty()) {
                // add the map from the mobile map package to the MapView
                mapview.map = mapPackage.maps[0]
            } else {
                // log an issue if the mobile map package fails to load
                logError(mapPackage.loadError.message)
            }
        }
    }

    /**
     * Log an error to logcat and to the screen via Toast.
     * @param message the text to log.
     */
    private fun logError(message: String?) {
        message?.let {
            Log.e(
                "TAG",
                message
            )
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupMap(){
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)
        mapview.map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)
        mapview.setViewpoint(defaultViewPoint)
    }

    override fun onPause() {
        mapview.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapview.resume()
    }

    override fun onDestroy() {
        mapview.dispose()
        super.onDestroy()
    }
}