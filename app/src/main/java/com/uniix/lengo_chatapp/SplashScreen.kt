package com.uniix.lengo_chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.uniix.lengo_chatapp.auth.LoginActivity
import com.uniix.lengo_chatapp.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var splashScreen : ActivitySplashScreenBinding
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashScreen.root)

        Thread {
            Thread.sleep(1000)
            if (auth.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }.start()
    }
}