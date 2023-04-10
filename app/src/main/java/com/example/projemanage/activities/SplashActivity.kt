package com.example.projemanage.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.projemanage.databinding.ActivitySplashBinding
import com.example.projemanage.firebase.FireStoreClass

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val typeFace: Typeface = Typeface.createFromAsset(assets, "Lovesmith Sans Bold.otf")
        binding.tvAppName.typeface = typeFace

        Handler(Looper.getMainLooper()).postDelayed({

            val currentUserID = FireStoreClass().getCurrentUserId()

            if (currentUserID.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))

            } else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2500)


    }


}