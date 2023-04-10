package com.example.projemanage.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.projemanage.R
import com.example.projemanage.databinding.ActivitySignInBinding
import com.example.projemanage.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        auth = FirebaseAuth.getInstance()


        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar()
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignInActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun signInRegisteredUser() {
        val email: String = binding.etEmailSignin.text.toString()
        val password: String = binding.etPasswordSignin.text.toString()

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()

                    if (task.isSuccessful) {
                        Log.d("Sign in", "signInWithEmail: success")
                        val user = auth.currentUser
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))

                    } else {
                        Log.w("Sign in", "signInWithEmail: failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter  password")
                false
            }
            else -> {
                true
            }
        }

    }
}