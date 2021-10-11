package com.alpha.a004_display_scene

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpha.a004_display_scene.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Surface
import com.esri.arcgisruntime.mapping.view.Camera
import com.esri.arcgisruntime.mapping.view.SceneView

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val sceneView: SceneView by lazy { binding.scene }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()
    }

    private fun setupMap(){
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        val scene = ArcGISScene(Basemap.createImagery())

        sceneView.scene = scene

        // add base surface for elevation data that contains the three dimensional layers
        val elevationSource = ArcGISTiledElevationSource("https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer")
        val surface = Surface(listOf(elevationSource))

        // factor to increase the 3D effect of the elevation (Sharpness)
        surface.elevationExaggeration = 2.5f

        scene.baseSurface = surface

        sceneView.setViewpointCamera(setupCamera())
    }

    private fun setupCamera(): Camera {
        // Point(x, y, z, spatialReference)
        val cameraLocation = Point(-118.794, 33.909, 5330.0, SpatialReferences.getWgs84())

        // Camera(location, heading, pitch, roll)
        // Camera(location, z, up-down, side-to-side)
        return Camera(cameraLocation, 355.0, 72.0, 0.0)
    }

    override fun onPause() {
        sceneView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        sceneView.resume()
    }

    override fun onDestroy() {
        sceneView.dispose()
        super.onDestroy()
    }
}