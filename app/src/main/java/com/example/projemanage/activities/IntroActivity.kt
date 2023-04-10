package com.example.projemanage.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import com.example.projemanage.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val typeFace: Typeface = Typeface.createFromAsset(
            assets,
            "Coco-Gothic-Bold-trial.ttf"
        )

        binding.tvAppNameIntro.typeface = typeFace

        binding.btnSignUpIntro.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }

        binding.btnSignInIntro.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }


    }
}