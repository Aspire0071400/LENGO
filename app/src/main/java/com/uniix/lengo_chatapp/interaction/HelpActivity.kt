package com.uniix.lengo_chatapp.interaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.uniix.lengo_chatapp.MainActivity
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var helpActivity: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helpActivity = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(helpActivity.root)

        setSupportActionBar(helpActivity.toolbarHelp)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onBackPressed() {
        finish()
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}