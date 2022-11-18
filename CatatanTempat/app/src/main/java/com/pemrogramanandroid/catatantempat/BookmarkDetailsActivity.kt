package com.pemrogramanandroid.catatantempat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.pemrogramanandroid.catatantempat.databinding.BookmarkDetailBinding
import com.pemrogramanandroid.catatantempat.viewmodel.BookmarkDetailsViewModel

class BookmarkDetailsActivity: AppCompatActivity() {

    //view binding
    private lateinit var binding: BookmarkDetailBinding
    private val bookmarkDetailsViewModel by viewModels<BookmarkDetailsViewModel>()

    //data binding
    private var bookmarkDetailsView: BookmarkDetailsViewModel.BookmarkDetailsView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.bookmarkDetail)
        setupToolbar()
        getIntentData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bookmark_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            saveChanges()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun saveChanges() {
        val name = binding.editTextName.text.toString()
        if (name.isEmpty()) {
            return
        }
        bookmarkDetailsView?.let { bookmarkView ->
            bookmarkView.name = binding.editTextName.text.toString()
            bookmarkView.notes = binding.editTextNotes.text.toString()
            bookmarkView.address = binding.editTextAddress.text.toString()
            bookmarkView.phone = binding.editTextPhone.text.toString()
            bookmarkDetailsViewModel.updateBookmark(bookmarkView)
        }
        finish()
    }

    //read intent data from Maps Activity to populate UI
    private fun getIntentData() {
        val bookmarkId = intent.getLongExtra(
            MapsActivity.EXTRA_BOOKMARK_ID, 0
        )

        bookmarkDetailsViewModel.getBookmark(bookmarkId)?.observe(this, {
            it?.let {
                bookmarkDetailsView = it
                binding.bookmarkDetailsView = it
                populateImageView()
            }
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun populateImageView() {
        bookmarkDetailsView?.let { bookmarkView ->
            val placeImage = bookmarkView.getImage(this)
            placeImage?.let {
                binding.imageViewPlace.setImageBitmap(placeImage)
            }
        }
    }
}