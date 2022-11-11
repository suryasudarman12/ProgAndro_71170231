package com.pemrogramanandroid.catatantempat

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.BaseAdapter

import androidx.activity.viewModels
import androidx.core.app.ActivityCompat

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.pemrogramanandroid.catatantempat.Adapter.BookmarkInfoWindowAdapter
import com.pemrogramanandroid.catatantempat.databinding.ActivityMapsBinding
import com.pemrogramanandroid.catatantempat.viewmodel.MapsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private var arraySpinner = ArrayList<String>()

    private val mapsViewModel by viewModels<MapsViewModel>()



    companion object {
        private const val TAG = "MapsActivity"
        private const val REQUEST_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySpinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bookmarkList.adapter = spinnerAdapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupPlacesClient()
    }
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION
            )
        } else {
            mMap.isMyLocationEnabled = true

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }).addOnSuccessListener { location: Location? ->
                if (location == null)
                    Log.e(TAG, "No location found: assign default location")
                else {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            16.0f
                        )
                    )
                }
            }
        }
    }

    private fun setupPlacesClient() {
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
    }

    private fun displayPoi(poi: PointOfInterest) {
        val placeId = poi.placeId
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.PHOTO_METADATAS,
            Place.Field.PHONE_NUMBER,
            Place.Field.LAT_LNG
        )

        val request = FetchPlaceRequest
            .builder(placeId, placeFields)
            .build()

        placesClient.fetchPlace(request)
            .addOnCompleteListener { response ->
                val place = response.result.place
                displayPointInfo(place)
            }
            .addOnFailureListener {
                if (it is ApiException) {
                    Log.e(TAG, "displayPoi Exception: " + it.message + ", error Code: " + it.statusCode)
                }
            }
        }

        private fun displayPointInfo(place : Place){
            val photoMetadata = place.photoMetadatas?.get(0)

            if (photoMetadata == null){
                displayPoiPhoto(place,null)
                return
            }

            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxHeight(resources.getDimensionPixelSize(R.dimen.img_default_height))
                .setMaxWidth(resources.getDimensionPixelSize(R.dimen.img_default_widht))
                .build()
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener {
                    val bitmap = it.bitmap
                    displayPoiPhoto(place, bitmap)
                }.addOnFailureListener {
                if (it is ApiException) {
                    Log.e(TAG, "displayPoiInfo Exception: " + it.message + ", error Code:" + it.statusCode)
                }
            }
        }

        private fun displayPoiPhoto(place: Place, photo : Bitmap?){
            val marker = mMap.addMarker(
                MarkerOptions().position(place.latLng)
                    .title(place.name)
                    .snippet(place.phoneNumber)
            )
            marker?.tag = PlaceInfo(place,photo)

        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_LOCATION) {
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: location permission granted")
                } else {
                    Log.e(TAG, "onRequestPermissionsResult: location permission denied")
                }
            }
        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap

            setupMapListener()
            createBookmarkMarkerObserver()
            getCurrentLocation()

        }
    private fun createBookmarkMarkerObserver() {
        mapsViewModel.getBookmarkMarkerViews()?.observe(
            this, {
                mMap.clear()
                it?.let {
                    displayAllBookmarks(it)
                }
            })
    }
    private fun displayAllBookmarks(bookmarks: List<MapsViewModel.BookmarkMarkerView>) {
        arraySpinner.clear()
        arraySpinner.add("My Location")
        bookmarks.forEach {
            addPlaceMarker(it)
            arraySpinner.add(it.name.toString())
        }
        (binding.bookmarkList.adapter as BaseAdapter).notifyDataSetChanged()
    }
    private fun addPlaceMarker(bookmark: MapsViewModel.BookmarkMarkerView): Marker? {
        val marker = mMap.addMarker(
            MarkerOptions()
                .position(bookmark.location)
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE
                    )
                )
                .alpha(0.8f)
        )
        marker?.tag = bookmark
        return marker
    }

        private fun setupMapListener() {
            mMap.setInfoWindowAdapter(BookmarkInfoWindowAdapter(this))
            mMap.setOnPoiClickListener {
                displayPoi(it)
            }
            mMap.setOnInfoWindowClickListener {
                handleInfoWindowClick(it)
            }

        }

    private fun handleInfoWindowClick(marker: Marker?) {
        val placeInfo = (marker?.tag as PlaceInfo)
        if (placeInfo.place != null) {
            GlobalScope.launch {
                mapsViewModel.addBookmarkFromPlace(placeInfo.place, placeInfo.image)
            }
        }
        marker.remove()
    }

    class PlaceInfo(val place: Place? = null, val image: Bitmap? = null)
}


