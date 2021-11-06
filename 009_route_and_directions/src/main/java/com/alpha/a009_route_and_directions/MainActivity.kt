package com.alpha.a009_route_and_directions

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.alpha.a009_route_and_directions.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask
import com.esri.arcgisruntime.tasks.networkanalysis.Stop
import kotlin.math.roundToInt

// Ref: https://developers.arcgis.com/android/route-and-directions/tutorials/find-a-route-and-directions/

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val routeStops: MutableList<Stop> by lazy { mutableListOf() }

    private val graphicsOverlay: GraphicsOverlay by lazy { GraphicsOverlay() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()
    }

    private fun setupMap() {
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        binding.mapView.apply {
            this.map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)
            this.setViewpoint(Viewpoint(34.0539, -118.2453, 144447.638572))
            this.graphicsOverlays.add(graphicsOverlay)
        }

        setTouchListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener() {
        binding.mapView.onTouchListener = object : DefaultMapViewOnTouchListener(this, binding.mapView){
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val screenTouch = Point(e.x.roundToInt(), e.y.roundToInt())
                val stop = Stop(binding.mapView.screenToLocation(screenTouch))
                when(routeStops.size){
                    0 -> addStop(stop)
                    1 -> {
                        addStop(stop)
                        findRoute()
                    }
                    else -> {
                        clear()
                        addStop(stop)
                    }
                }

                return true
            }
        }
    }

    private fun addStop(stop: Stop){
        routeStops.add(stop)
        val simpleMarker = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 15f)
        graphicsOverlay.graphics.add(Graphic(stop.geometry, simpleMarker))
    }

    private fun findRoute(){
        val routeTask = RouteTask(this, "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World")
        val routeParametersFuture = routeTask.createDefaultParametersAsync()
        routeParametersFuture.addDoneListener {
            if(routeParametersFuture.isDone){
                val routeParameters = routeParametersFuture.get().apply {
                    isReturnDirections = true
                    setStops(routeStops)
                    directionsLanguage = "ar"
                }

                val routeTaskFuture = routeTask.solveRouteAsync(routeParameters)
                routeTaskFuture.addDoneListener {
                    if(routeTaskFuture.isDone){
                        val routeResult = routeTaskFuture.get()
                        if(routeResult.routes.isEmpty()) return@addDoneListener
                        routeResult.routes[0].directionManeuvers
//                            .map{ it.directionText }
                            .forEachIndexed { index, direction ->
                                val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, 5f)
                                val routeGraphic = Graphic(direction.geometry, simpleMarkerSymbol)
                                graphicsOverlay.graphics.add(routeGraphic)
                                Log.d("DIRECTIONS_TAG", "$index: ${direction.directionText}")
                            }
                    }
                }
            }
        }
    }

    private fun clear() {
        routeStops.clear()
        graphicsOverlay.graphics.clear()
    }

}