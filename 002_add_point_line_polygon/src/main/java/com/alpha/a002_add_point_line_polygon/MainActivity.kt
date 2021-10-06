package com.alpha.a002_add_point_line_polygon

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpha.a002_add_point_line_polygon.databinding.ActivityMainBinding
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol

/*
Ref: https://developers.arcgis.com/android/maps-2d/tutorials/add-a-point-line-and-polygon/
 */
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mapview: MapView by lazy { binding.map }

    private var defaultViewPoint: Viewpoint = Viewpoint(30.052697, 31.198192, 72000.0)

    private val graphicsOverlay by lazy { GraphicsOverlay() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()

        // Adding point with outline
        addPoint()

        // Adding line
        addLine()

        // Adding polygon
        addPolygon()
    }

    private fun setupMap(){
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)
        mapview.map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)
        mapview.setViewpoint(defaultViewPoint)

        mapview.graphicsOverlays.add(graphicsOverlay)
    }

    private fun addPoint(){
        // create a point geometry with a location and spatial reference
        // Point(x -> longitude, y -> latitude, spatial reference)
        val point = Point(31.198192, 30.052697, SpatialReferences.getWgs84())

        // create an opaque orange point symbol with a blue outline symbol
        val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
            Color.argb(0xFF, 0xFF, 128, 0),
            10f)

        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
            -0xff9c01,
            1f)
        simpleMarkerSymbol.outline = blueOutlineSymbol

        // create a graphic with the point geometry and symbol
        val pointGraphic = Graphic(point, simpleMarkerSymbol)

        // add the point graphic to the graphics overlay
        graphicsOverlay.graphics.add(pointGraphic)
    }

    private fun addLine() {
        // create line points every point connect with next one
        val pointCollection = PointCollection(SpatialReferences.getWgs84()).apply {
            add(31.207476, 30.060592)
            add(31.190438, 30.059032)
            add(31.193571, 30.045065)
            add(31.207476, 30.060592)
        }

        // create a polyline geometry from the point collection
        val polyline = Polyline(pointCollection)

        // creating polyline style
        val polySymbol = SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, Color.MAGENTA, 2f)

        val lineGraphic = Graphic(polyline, polySymbol)
        graphicsOverlay.graphics.add(lineGraphic)
    }

    private fun addPolygon() {
        // create line points every point connect with next one
        // polygon will connect last point with the first one (closed boundary)
        val pointCollection = PointCollection(SpatialReferences.getWgs84()).apply {
            add(31.207476, 30.060592)
            add(31.190438, 30.059032)
            add(31.193571, 30.045065)
        }

        val polygon = Polygon(pointCollection)

//        val polyOutline = SimpleLineSymbol(SimpleLineSymbol.Style.DOT, Color.BLUE, 1f)
        val polyFillSymbol = SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.GREEN, null)

        val polyGraphic = Graphic(polygon, polyFillSymbol)

        graphicsOverlay.graphics.add(polyGraphic)
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