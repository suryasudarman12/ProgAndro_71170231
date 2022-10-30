package com.pemrogramanandroid.clickrush

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var timer: CountDownTimer
    var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var time: TextView = findViewById(R.id.time_view)
        val button: Button = findViewById(R.id.btn_tap)

        button.setOnClickListener {
        score++
            var textview: TextView = findViewById(R.id.score_view) as TextView
            textview.text = "Your Score : $score"

            button.text = score.toString()
        }



        timer = object:CountDownTimer(60000,10){
            override fun onTick(remaining: Long) {
                time.text = remaining.toString()
            }

            override fun onFinish() {
                var text: TextView = findViewById(R.id.text_hasil) as TextView
                time.text = "0"
                text.text = "Time's up! Your score was: $score"
            }

        }
    }
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        val userScore : Int = score
        outState.putInt("savedInt", score)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val userInt : Int = savedInstanceState.getInt("savedInt",0)
        score = userInt


    }

    override fun onStart() {
        super.onStart()
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }
}
