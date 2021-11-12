package com.alpha.a013_manage_operational_layers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.alpha.a013_manage_operational_layers.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer
import com.esri.arcgisruntime.layers.Layer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.MapView

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mapView: MapView by lazy { binding.mapView }

    private val inactiveLayers = mutableListOf<Layer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()
    }

    private fun setupMap() {
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        val imageLayerElevation =
            ArcGISMapImageLayer("https://sampleserver5.arcgisonline.com/arcgis/rest/services/Elevation/WorldElevations/MapServer")
        val imageLayerCensus =
            ArcGISMapImageLayer("https://sampleserver5.arcgisonline.com/arcgis/rest/services/Census/MapServer")
        val imageLayerDamage =
            ArcGISMapImageLayer("https://sampleserver5.arcgisonline.com/arcgis/rest/services/DamageAssessment/MapServer")

        mapView.map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)
        mapView.map.operationalLayers.addAll(listOf(imageLayerElevation, imageLayerCensus, imageLayerDamage))

        mapView.setViewpoint(Viewpoint( 34.056295, -117.195800, 50000000.0))

        setupUI()
    }

    private fun setupUI() {
        mapView.apply {
            addAttributionViewLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                val layoutParams = binding.fab.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.bottomMargin += bottom - oldBottom
            }
            onTouchListener = object : DefaultMapViewOnTouchListener(this@MainActivity, mapView) {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                    if (binding.fab.isExpanded) {
                        binding.fab.isExpanded = false
                    }
                    return super.onTouch(view, event)
                }
            }
        }

        binding.fab.setOnClickListener {
            binding.fab.isExpanded = !binding.fab.isExpanded
        }

        binding.activeRecyclerView.apply {
            adapter = ActiveListAdapter(mapView.map.operationalLayers)

            ItemTouchHelper(
                DragCallback(
                    onItemMove = { oldPosition, newPosition ->
                        moveLayerFromToPosition(oldPosition, newPosition)
                    },
                    onItemSwiped = { position -> removeLayerFromMap(position) })
            ).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(this@MainActivity).apply { reverseLayout = true }
        }

        binding.inactiveRecyclerView.apply {
            adapter = InactiveListAdapter(inactiveLayers) { addLayerToMap(it) }
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun moveLayerFromToPosition(oldPosition: Int, targetPosition: Int) {
        val operationalLayers = mapView.map.operationalLayers
        val layer = operationalLayers.removeAt(oldPosition)
        operationalLayers.add(targetPosition, layer)
        binding.activeRecyclerView.adapter?.notifyItemMoved(oldPosition, targetPosition)
    }

    private fun removeLayerFromMap(position: Int) {
        val operationalLayers = mapView.map.operationalLayers
        inactiveLayers.add(operationalLayers[position])
        operationalLayers.removeAt(position)
        binding.activeRecyclerView.adapter?.notifyItemRemoved(position)
    }

    private fun addLayerToMap(position: Int) {
        val layer = inactiveLayers.removeAt(position)
        mapView.map.operationalLayers.add(layer)
        binding.inactiveRecyclerView.adapter?.notifyItemRemoved(position)
        binding.activeRecyclerView.adapter?.notifyItemInserted(mapView.map.operationalLayers.size)
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }
}