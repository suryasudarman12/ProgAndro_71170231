package com.pemrogramanandroid.clickrush

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val teks1 = findViewById<TextView>(R.id.textView1)
        val teks2 = findViewById<TextView>(R.id.textView2)
        println(teks1)
        println(teks2)
    }
}