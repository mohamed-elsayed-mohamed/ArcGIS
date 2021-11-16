package com.alpha.a014_scene_layer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpha.a014_scene_layer.databinding.ActivityMainBinding
import com.esri.arcgisruntime.layers.ArcGISSceneLayer
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Camera

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sceneLayer = ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer")
        val camera = Camera(48.378, -4.494, 200.0, 345.0, 65.0, 0.0)
        val scene = ArcGISScene(Basemap.createTopographic())
        scene.operationalLayers.add(sceneLayer)

        binding.sceneView.scene = scene
        binding.sceneView.setViewpointCamera(camera)
    }

    override fun onPause() {
        super.onPause()
        binding.sceneView.pause()
    }

    override fun onResume() {
        binding.sceneView.resume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.sceneView.dispose()
    }
}