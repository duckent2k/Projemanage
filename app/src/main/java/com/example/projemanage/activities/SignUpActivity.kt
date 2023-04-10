package com.example.projemanage.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.projemanage.R
import com.example.projemanage.databinding.ActivitySignUpBinding
import com.example.projemanage.firebase.FireStoreClass
import com.example.projemanage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "you have successfully registered the email address ",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }


    }

    private fun registerUser() {
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)

                        FireStoreClass().registerUser(this, user)

                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
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