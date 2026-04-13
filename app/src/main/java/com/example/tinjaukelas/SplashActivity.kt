package com.example.tinjaukelas  // sesuaikan dengan package kamu

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)

        val layoutSplash = findViewById<LinearLayout>(R.id.layoutSplash)
        val imgLogo      = findViewById<ImageView>(R.id.imgLogo)

        val scaleX     = ObjectAnimator.ofFloat(imgLogo, "scaleX", 0f, 1f)
        val scaleY     = ObjectAnimator.ofFloat(imgLogo, "scaleY", 0f, 1f)
        val fadeIn     = ObjectAnimator.ofFloat(imgLogo, "alpha", 0f, 1f)
        val slideUp    = ObjectAnimator.ofFloat(layoutSplash, "translationY", 100f, 0f)
        val fadeLayout = ObjectAnimator.ofFloat(layoutSplash, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, fadeIn, slideUp, fadeLayout)
            duration = 1000
            start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }
}