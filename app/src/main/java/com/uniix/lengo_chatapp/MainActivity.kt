package com.uniix.lengo_chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.uniix.lengo_chatapp.adapters.ScreenSlidePagerAdapter
import com.uniix.lengo_chatapp.books.BookReadingActivity
import com.uniix.lengo_chatapp.databinding.ActivityMainBinding
import com.uniix.lengo_chatapp.interaction.GroupVideoCallingActivity
import com.uniix.lengo_chatapp.interaction.HelpActivity
import com.uniix.lengo_chatapp.interaction.ProfileActivity
import com.uniix.lengo_chatapp.news.NewsReadingActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mainActivity : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivity.root)
        setSupportActionBar(mainActivity.toolbar)
        auth = FirebaseAuth.getInstance()
        mainActivity.viewPager.adapter = ScreenSlidePagerAdapter(this)
        TabLayoutMediator(mainActivity.tabs, mainActivity.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "CHATS"
                    }
                    1 -> {
                        tab.text = "PEOPLE"
                    }
                }
            }).attach()
        userStatus()
    }

    private fun userStatus() {
        db.reference.child("status/${auth.uid}").child("status").setValue("Online")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection and navigating/inflating to selected module
        return when (item.itemId) {
            R.id.profile -> {
                profile()
                true
            }
            R.id.newsImage -> {
                newsReader()
                true
            }
            R.id.books -> {
                booksReader()
                true
            }
            R.id.searchGroupVideoCall -> {
                groupVideoCall()
                true
            }
            R.id.help -> {
                help()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun profile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

   /* private fun todo() {
        startActivity(Intent(this, ToDoActivity::class.java))
    }*/

    private fun newsReader() {
        startActivity(Intent(this, NewsReadingActivity::class.java))
    }

    private fun booksReader() {
        startActivity(Intent(this, BookReadingActivity::class.java))
    }

    private fun groupVideoCall() {
        startActivity(Intent(this, GroupVideoCallingActivity::class.java))
    }

    private fun help() {
        startActivity(Intent(this, HelpActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        db.reference.child("status/${auth.uid}").child("status").setValue("Offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        db.reference.child("status/${auth.uid}").child("status").setValue("Offline")
    }
}