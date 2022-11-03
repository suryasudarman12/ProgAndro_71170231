package com.pemrogramanandroid.catatankecilku

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecylerAdapter(private var daftar: List<daftar>) :
    RecyclerView.Adapter<RecylerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDaftar: TextView = view.findViewById(R.id.tvCatatan)
    }

    override fun onCreateViewHolder(parent:ViewGroup,  viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDaftar.text = daftar[position].catatan
    }

    override fun getItemCount(): Int {
        return daftar.size
    }
}