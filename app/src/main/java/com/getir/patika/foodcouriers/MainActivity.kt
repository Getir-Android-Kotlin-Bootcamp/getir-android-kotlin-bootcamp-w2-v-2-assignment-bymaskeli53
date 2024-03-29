package com.getir.patika.foodcouriers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapCapabilities
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //    private lateinit var tabLayout: TabLayout
//    private lateinit var viewPager2: ViewPager2
//    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var map: GoogleMap
    private lateinit var circle: Circle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        tabLayout = findViewById(R.id.tab_account)
//        viewPager2 = findViewById(R.id.viewpager_account)
//        pagerAdapter = PagerAdapter(supportFragmentManager,lifecycle).apply {
//            addFragment(CreateAccountFragment())
//            addFragment(LoginAccountFragment())
//        }
//        viewPager2.adapter = pagerAdapter
//
//        TabLayoutMediator(tabLayout,viewPager2){ tab, position ->
//             when(position) {
//                 0 -> {
//                     tab.text = "Create Account"
//                 }
//                 1 -> {
//                     tab.text = "Login Account"
//                 }
//             }
//
//        }.attach()

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val sydney = LatLng(-33.852, 151.211)
        val bitmap = generateBitmapDescriptorFromRes(this, R.drawable.icon_marker)
       // val bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_marker)
        val options = MarkerOptions().apply {
            position(sydney)
            title("Sydney")
            // icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

            icon(bitmap)
        }

//        }.also {
//            icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker))
//        }
        map.addMarker(options)
        val capabilities: MapCapabilities = map.getMapCapabilities()
        if (capabilities.isAdvancedMarkersAvailable) {
            println("Hi")
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17.0f))



        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        val circlePosition = LatLng(sydney.latitude, sydney.longitude )

        drawCircle(circlePosition,100.0)



    }
    private fun drawCircle(center: LatLng, radius: Double) {
        // Remove previous circle if exists
        if (::circle.isInitialized) {
            circle.remove()
        }

        // Draw circle overlay
        circle = map.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(70, 255, 0, 0))
        )
    }
}

fun generateBitmapDescriptorFromRes(
    context: Context?, resId: Int
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context!!, resId)
    drawable!!.setBounds(
        0,
        0,
        drawable.intrinsicWidth,
        drawable.intrinsicHeight
    )
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

