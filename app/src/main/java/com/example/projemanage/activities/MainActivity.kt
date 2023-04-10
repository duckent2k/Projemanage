package com.example.projemanage.activities


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanage.R
import com.example.projemanage.adapters.BoardItemsAdapter
import com.example.projemanage.databinding.ActivityMainBinding
import com.example.projemanage.firebase.FireStoreClass
import com.example.projemanage.models.Board
import com.example.projemanage.models.User
import com.example.projemanage.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    private lateinit var mUserName: String
    private lateinit var mSharePreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener(this)

        mSharePreferences =
            this.getSharedPreferences(Constants.PROJEMANAG_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdate  = mSharePreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if (tokenUpdate){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().loadUserData(this, true)
        }else {
            FirebaseInstallations.getInstance().getToken(true)
                .addOnSuccessListener(this@MainActivity) {
                    result ->
                    updateFCMToken(result.token)
                }
        }


        FireStoreClass().loadUserData(this, true)

        val fabCreateBoard = findViewById<FloatingActionButton>(R.id.fabCreateBoard)
        fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)

            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }


    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()

        val rvBoardsList = findViewById<RecyclerView>(R.id.rvBoardsList)
        val tvNoBoardsAvailable = findViewById<TextView>(R.id.tvNoBoardsAvailable)



        if (boardsList.size > 0) {
            rvBoardsList.visibility = View.VISIBLE
            tvNoBoardsAvailable.visibility = View.GONE

            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            rvBoardsList.adapter = adapter


            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)

                }
            })

        } else {
            rvBoardsList.visibility = View.GONE
            tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }


    private fun setupActionBar() {
        val toolbarMainActivity = findViewById<Toolbar>(R.id.toolbarMainActivity)
        setSupportActionBar(toolbarMainActivity)
        toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }


    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {
        hideProgressDialog()
        mUserName = user.name


        val navUserImage = findViewById<CircleImageView>(R.id.nav_user_image)
        val tvUserName = findViewById<TextView>(R.id.tvUsername)


        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        tvUserName.text = user.name

        if (readBoardsList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardsList(this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FireStoreClass().loadUserData(this@MainActivity)
        } else if (requestCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            FireStoreClass().getBoardsList(this)
        } else {
            Log.e("Canceled", "Canceled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(
                    Intent(this@MainActivity, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )

            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                mSharePreferences.edit().clear().apply()

                val intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }


        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharePreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadUserData(this, true)
    }

    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateUserProfileData(this, userHashMap)
    }

}