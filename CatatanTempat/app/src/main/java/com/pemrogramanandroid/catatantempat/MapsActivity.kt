package com.pemrogramanandroid.catatantempat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.BaseAdapter

import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager

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
import com.pemrogramanandroid.catatantempat.Adapter.BookmarkListAdapter
import com.pemrogramanandroid.catatantempat.databinding.ActivityMapsBinding
import com.pemrogramanandroid.catatantempat.viewmodel.MapsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    private val mapsViewModel by viewModels<MapsViewModel>()

    private lateinit var bookmarkListAdapter: BookmarkListAdapter

    private var markers = HashMap<Long, Marker>()


    companion object {
        private const val TAG = "MapsActivity"
        private const val REQUEST_LOCATION = 1
        const val EXTRA_BOOKMARK_ID = "com.pemrogandroid.catatantempat.EXTRA_BOOKMARK_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySpinner)
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.bookmarkList.adapter = spinnerAdapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupPlacesClient()
        setupNavigationDrawer()
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.mainMapView.toolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.mainMapView.toolbar,
            R.string.open_drawer, R.string.close_drawer)
        toggle.syncState()
    }

    private fun setupNavigationDrawer() {
        val layoutManager = LinearLayoutManager(this)
        binding.drawerViewMaps.bookmarkRecyclerView.layoutManager = layoutManager
        bookmarkListAdapter = BookmarkListAdapter(null, this)
        binding.drawerViewMaps.bookmarkRecyclerView.adapter = bookmarkListAdapter
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
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
        )

        //request query
        val request = FetchPlaceRequest
            .builder(placeId, placeFields)
            .build()

        placesClient.fetchPlace(request)
            .addOnCompleteListener { response ->
                val place = response.result.place
                displayPoiInfo(place)
            }
            .addOnFailureListener {
                if (it is ApiException) {
                    Log.e(TAG, "displayPoi Exception: " + it.message + ", error Code:" + it.statusCode)
                }
            }
    }

    private fun displayPoiInfo(place: Place) {
        val photoMetadata = place.getPhotoMetadatas()?.get(0)

        if (photoMetadata == null) {
            displayPoiPhoto(place, null)
            return
        }

        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxHeight(resources.getDimensionPixelSize(R.dimen.img_default_height))
            .setMaxWidth(resources.getDimensionPixelSize(R.dimen.img_default_width))
            .build()
        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener {
                displayPoiPhoto(place, it.bitmap)
            }.addOnFailureListener {
                if (it is ApiException) {
                    Log.e(TAG, "displayPoiInfo Exception: " + it.message + ", error Code:" + it.statusCode)
                }
            }
    }

    private fun displayPoiPhoto(place: Place, photo: Bitmap?) {
        val marker = mMap.addMarker(
            MarkerOptions().position(place.latLng as LatLng)
                .title(place.name)
                .snippet(place.phoneNumber)
        )
        marker?.tag = PlaceInfo(place, photo)
        marker?.showInfoWindow()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: location permission granted")
            } else {
                Log.e(TAG, "onRequestPermissionsResult: location permission denied")
            }
        }
    }

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
        bookmarks.forEach {
            addPlaceMarker(it)
        }
    }

    private fun addPlaceMarker(bookmark: MapsViewModel.BookmarkMarkerView): Marker? {
        val marker = mMap.addMarker(
            MarkerOptions()
                .position(bookmark.location)
                .title(bookmark.name)
                .snippet(bookmark.phone)
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
        when (marker?.tag) {
            is MapsActivity.PlaceInfo -> {
                val placeInfo = (marker?.tag as PlaceInfo)
                if (placeInfo.place != null) {
                    GlobalScope.launch {
                        mapsViewModel.addBookmarkFromPlace(placeInfo.place, placeInfo.image)
                    }
                }
                marker.remove()
            }

            is MapsViewModel.BookmarkMarkerView -> {
                val bookmarView = marker.tag as MapsViewModel.BookmarkMarkerView
                marker.hideInfoWindow()
                bookmarView.id?.let {
                    startBookmarkDetails(it)
                }
            }
        }

    }

    private fun startBookmarkDetails(bookmarkId: Long) {
        //call bookmarkdetail activity
        val intent = Intent(this, BookmarkDetailsActivity::class.java)
        intent.putExtra(EXTRA_BOOKMARK_ID, bookmarkId)
        startActivity(intent)
    }

    private fun updateMapToLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
    }

    fun moveToBookmark(bookmark: MapsViewModel.BookmarkMarkerView) {
        binding.drawerLayout.closeDrawer(binding.drawerViewMaps.drawerView)
        val marker = markers[bookmark.id]
        marker?.showInfoWindow()
        val location = Location("")
        location.latitude = bookmark.location.latitude
        location.longitude = bookmark.location.longitude
        updateMapToLocation(location)
    }

    class PlaceInfo(val place: Place? = null, val image: Bitmap? = null)
}


