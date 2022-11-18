package com.pemrogramanandroid.catatantempat.Adapter

import android.app.Activity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.pemrogramanandroid.catatantempat.MapsActivity
import com.pemrogramanandroid.catatantempat.databinding.PlacesBinding
import com.pemrogramanandroid.catatantempat.viewmodel.MapsViewModel



class BookmarkInfoWindowAdapter(val context: Activity) :
    GoogleMap.InfoWindowAdapter {

    private val binding =PlacesBinding.inflate(context.layoutInflater)


    override fun getInfoContents(marker: Marker): View? {
        binding.title.text = marker.title ?: ""
        binding.phone.text = marker.snippet ?: ""

        val imageView = binding.photo
        when(marker.tag){
            is MapsActivity.PlaceInfo->{
                imageView.setImageBitmap((marker.tag as MapsActivity.PlaceInfo).image)
            }
            is MapsViewModel.BookmarkMarkerView ->{
                val bookmarkView = marker.tag as MapsViewModel.BookmarkMarkerView
                imageView.setImageBitmap(bookmarkView.getImage(context))

            }

        }
        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}