package com.getir.patika.foodcouriers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle

import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.getir.patika.foodcouriers.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

import java.util.Locale


class MainActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var map: GoogleMap
    private lateinit var options: MarkerOptions


    private lateinit var circle: Circle

    private lateinit var binding: ActivityMainBinding

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = binding.searchView.query.toString()
                val listOfAddresses: MutableList<Address>

                val geocoderToNavigate = Geocoder(this@MainActivity)
                @Suppress("DEPRECATION")
                listOfAddresses = geocoderToNavigate.getFromLocationName(location, 1)!!


                val address = listOfAddresses.get(0)
                val latLng = LatLng(address.latitude, address.longitude)

                options = options.also {
                    it.position(latLng)
                    it.title(location)
                }


                map.addMarker(options)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                drawCircle(latLng)
                val geocoder: Geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                var addresses: MutableList<Address>
                try {
                    @Suppress("DEPRECATION")
                    addresses = geocoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude,
                        1
                    )!!
                    binding.tvAddress.text = addresses.get(0).getAddressLine(0)


                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return false
            }
        })



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()


    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return

        }

        val task: Task<Location> = fusedLocationProviderClient.getLastLocation()

        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location

                val geocoder: Geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                var addresses: MutableList<Address>


                try {
                    @Suppress("DEPRECATION")
                    addresses = geocoder.getFromLocation(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        1
                    )!!
                    binding.tvAddress.text = addresses.get(0).getAddressLine(0)


                } catch (e: Exception) {
                    e.printStackTrace()
                }


                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                mapFragment?.getMapAsync(this@MainActivity)
            }
        }


    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        val bitmap = generateBitmapDescriptorFromRes(this, R.drawable.icon_marker)

        options = MarkerOptions().apply {
            position(myLocation)
            title("My Location")


            icon(bitmap)
        }



        map.addMarker(options)


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18.0f))



        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        val circlePosition = LatLng(myLocation.latitude, myLocation.longitude)

        drawCircle(circlePosition)


    }

    private fun drawCircle(center: LatLng, radius: Double = 100.0) {

        if (::circle.isInitialized) {
            circle.remove()
        }

        // Draw circle overlay
        circle = map.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(70, 214, 19, 85))
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Location Permission is denied", Toast.LENGTH_LONG).show()
            }
        }
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

