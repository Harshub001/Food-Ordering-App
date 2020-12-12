package com.food.foodsensations.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.food.foodsensations.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val startAct= Intent(this@SplashActivity,
                LoginActivity::class.java)
            startActivity(startAct)
        },1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
