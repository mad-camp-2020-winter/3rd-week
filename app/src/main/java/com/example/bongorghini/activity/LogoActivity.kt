package com.example.bongorghini.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity

import com.example.bongorghini.MainActivity
import com.example.bongorghini.R

class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)
        val actionBar = supportActionBar
        actionBar!!.hide()

//        val mAnim = AnimationUtils.loadAnimation(
//            applicationContext,
//            R.anim.rotate
//        )
//
//        mAnim.setInterpolator(applicationContext, R.anim.accelerate_interpolator)
//
//        val mediaPlayer: MediaPlayer =
//            MediaPlayer.create(applicationContext, R.raw.splash_music)
//        mediaPlayer.start()

        val handler = Handler()
        handler.postDelayed({
            val intent =
                Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}