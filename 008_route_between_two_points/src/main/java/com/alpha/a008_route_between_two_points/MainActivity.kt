package com.alpha.a008_route_between_two_points

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alpha.a008_route_between_two_points.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask
import com.esri.arcgisruntime.tasks.networkanalysis.Stop

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mapView: MapView by lazy { binding.mapView }

    private val graphicsOverlay: GraphicsOverlay by lazy { GraphicsOverlay() }

    private val routeGraphic = Graphic()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupGraphics()
        setupMap()
        solveRouteTask()
    }

    private fun setupGraphics() {
        val originSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.WHITE, 12f).apply {
            outline =
                SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2f)
        }
        val originGraphic =
            Graphic(Point(-122.690176, 45.522054, SpatialReferences.getWgs84()), originSymbol)

        val stopSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.WHITE, 8f).apply {
            outline = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2f)
        }
        val stopGraphic =
            Graphic(Point(-122.614995, 45.526201, SpatialReferences.getWgs84()), stopSymbol)

        val destinationSymbol =
            SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLACK, 12f).apply {
                outline = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2f)
            }
        val destinationGraphic =
            Graphic(Point(-122.68782, 45.51238, SpatialReferences.getWgs84()), destinationSymbol)

        routeGraphic.apply {
            symbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.CYAN, 4f)
        }

        graphicsOverlay.apply {
            graphics.addAll(listOf(routeGraphic, originGraphic, stopGraphic, destinationGraphic))
        }
    }

    private fun setupMap() {

        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        val map = ArcGISMap(BasemapStyle.ARCGIS_NAVIGATION)

        mapView.apply {
            // set the map to be displayed in the layout's MapView
            this.map = map
            // set the viewpoint, Viewpoint(latitude, longitude, scale)
            setViewpoint(Viewpoint(45.53, -122.65, 144447.0))
            graphicsOverlays.add(graphicsOverlay)
        }
    }

    private fun solveRouteTask() {
        val stops: List<Stop> = graphicsOverlay.graphics
            .filter { graphic: Graphic -> graphic.geometry != null }
            .map { graphic: Graphic -> Stop(graphic.geometry as Point) }

        val routeTask = RouteTask(this, "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World")

        val routeParametersFuture = routeTask.createDefaultParametersAsync()
        routeParametersFuture.addDoneListener {
            try {
                val routeParameters = routeParametersFuture.get()
                routeParameters.apply {
                    setStops(stops)
                    isReturnDirections = true
                    directionsLanguage = "es"
                }

                val routeResultFuture = routeTask.solveRouteAsync(routeParameters)
                routeResultFuture.addDoneListener {
                    try {
                        val routeResult = routeResultFuture.get()
                        val route = routeResult.routes[0]
                        routeGraphic.geometry = route.routeGeometry

                        route.directionManeuvers.forEach { step -> Log.i("Route Directions", step.directionText)}
                    } catch (exception: Exception) {
                        Log.e(MainActivity::class.simpleName, exception.message!!)
                    }
                }
            } catch (exception: Exception) {
                Log.e(MainActivity::class.simpleName, exception.message!!)
            }
        }
    }
}