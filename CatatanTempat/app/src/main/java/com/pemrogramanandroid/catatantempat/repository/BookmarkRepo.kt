package com.pemrogramanandroid.catatantempat.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.pemrogramanandroid.catatantempat.db.PlaceBookDatabase
import com.pemrogramanandroid.catatantempat.model.Bookmark

class BookmarkRepo(private val context: Context) {
    private val db: PlaceBookDatabase = PlaceBookDatabase.getInstance(context)
    private val bookmarkDao: BookmarkDao = db.bookmarkDao()

    fun addBookmark(bookmark: Bookmark): Long? {
        val newId = bookmarkDao.insertBookmark(bookmark)
        bookmark.id = newId
        return newId
    }

    fun createBookmark(): Bookmark {
        return Bookmark()
    }

    val allBookmarks: LiveData<List<Bookmark>>
        get() {
            return bookmarkDao.loadAll()
        }
}