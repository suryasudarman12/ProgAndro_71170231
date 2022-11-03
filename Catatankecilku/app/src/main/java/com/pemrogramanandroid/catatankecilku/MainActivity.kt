package com.pemrogramanandroid.catatankecilku

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val rvDaftar = findViewById<RecyclerView>(R.id.rv_recyler)
        val fab: View = findViewById(R.id.fab_btn)
        val etTitle = findViewById<TextView>(R.id.tvCatatan)

        val daftarList = mutableListOf(
            daftar("1. daftar belanja"),
            daftar("2. daftar matakuliah"),
        )

        val adapter = RecylerAdapter(daftarList)
        rvDaftar.adapter = adapter
        rvDaftar.layoutManager = LinearLayoutManager(this)


       fab.setOnClickListener {
               val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
               builder.setTitle("Masukan nama daftar anda ?")
               val input = EditText(this)
               val Daftar = daftar("")

               daftarList.add(Daftar)
               adapter.notifyItemInserted(daftarList.size - 1)


               input.setHint("Enter Text")
               input.inputType = InputType.TYPE_CLASS_TEXT
               builder.setView(input)

               builder.setPositiveButton("CREATE", DialogInterface.OnClickListener { dialog, which ->
                   var m_Text = input.text.toString()

               })
               builder.show()
           }
       }







}