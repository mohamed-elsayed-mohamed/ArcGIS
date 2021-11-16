package com.alpha.a015_feature_layer_extrusion

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpha.a015_feature_layer_extrusion.databinding.ActivityMainBinding
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Camera
import com.esri.arcgisruntime.mapping.view.OrbitLocationCameraController
import com.esri.arcgisruntime.symbology.Renderer
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleRenderer

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val serviceFeatureTable = ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3")
        val featureLayer = FeatureLayer(serviceFeatureTable)
        featureLayer.renderingMode = FeatureLayer.RenderingMode.DYNAMIC

        val scene = ArcGISScene(Basemap.createImagery())
        binding.sceneView.scene = scene

        scene.operationalLayers.add(featureLayer)

        val simpleLine = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 5f)
        val simpleFill = SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.MAGENTA, simpleLine)
        val renderer = SimpleRenderer(simpleFill)

        // set renderer extrusion mode to base height, which includes base height of each vertex in calculating z values
        renderer.sceneProperties.extrusionMode = Renderer.SceneProperties.ExtrusionMode.BASE_HEIGHT

        featureLayer.renderer = renderer

        // define a look at point for the camera at geographical center of the continental US
        val lookAtPoint = Point(-10974490.0, 4814376.0, 0.0, SpatialReferences.getWebMercator())

        // add a camera and set it to orbit the look at point
        val camera = Camera(lookAtPoint, 20000000.0, 0.0, 55.0, 0.0)

        // Represents a camera controller that restricts the scene view's camera to orbit a fixed location.
        // Interactions on the scene view will pivot the camera around a target location so the camera is always looking at the point. When active, setting the viewpoint on the scene view will be disabled.
        val orbitCamera = OrbitLocationCameraController(lookAtPoint, 20000000.0)
        binding.sceneView.cameraController = orbitCamera
        binding.sceneView.setViewpointCamera(camera)

        binding.switchPopulation.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                // multiple population density by 5000 to make data legible
                renderer.sceneProperties.extrusionExpression = "[POP07_SQMI] * 5000"
            } else {
                // divide total population by 10 to make data legible
                renderer.sceneProperties.extrusionExpression = "[POP2007] / 10"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.sceneView.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.sceneView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.sceneView.dispose()
    }
}