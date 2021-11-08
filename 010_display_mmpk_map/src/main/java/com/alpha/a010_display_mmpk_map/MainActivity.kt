package com.alpha.a010_display_mmpk_map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alpha.a010_display_mmpk_map.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.MobileMapPackage
import com.esri.arcgisruntime.mapping.Viewpoint
import java.io.File
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val filename: String = "MahouRivieraTrails.mmpk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        loadMobileMapPackage()
    }

    private fun loadMobileMapPackage() {

        val file = File(filename)

        val mobileMapPackage = MobileMapPackage(getFileFromAssets(filename)?.absolutePath)

        mobileMapPackage.loadAsync()

        mobileMapPackage.addDoneLoadingListener {
            if(mobileMapPackage.loadStatus == LoadStatus.LOADED && mobileMapPackage.maps.isNotEmpty()){
                binding.mapView.map = mobileMapPackage.maps.first()
            } else {
                Log.d("MMPKTAG", "Can't load MMPK file")
            }
        }
    }

    private fun getFileFromAssets(fileName: String): File? {
        return try {
            File(this.cacheDir, fileName)
                .also {
                    it.outputStream()
                        .use { cache ->
                            this.assets.open(fileName)
                                .use { inputStream -> inputStream.copyTo(cache) }
                        }
                }
        } catch (exception: FileNotFoundException){
            null
        }

    }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.dispose()
    }
}