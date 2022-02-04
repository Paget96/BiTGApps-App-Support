package com.paget96.bitgapps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private fun initializeView() {
        setContentView(R.layout.activity_splash_screen)

        // Set up a animation and start background work
        // after the animation is finished
        val logo = findViewById<ImageView>(R.id.logo)
        val fadeIn: Animation = AnimationUtils.loadAnimation(this@SplashScreen, R.anim.fade_in)
        logo.animation = fadeIn
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                val mainActivity = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(mainActivity)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dynamic color changing
        DynamicColors.applyIfAvailable(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        initializeView()
    }
}