package com.uniix.lengo_chatapp.books

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ActivityBookDetailsReadingBinding

const val BOOKS_URL = "URL"

class BookDetailsReadingActivity : AppCompatActivity() {

    // Initializing Variables
    private lateinit var bookDetailsReadingActivity: ActivityBookDetailsReadingBinding
    private lateinit var booksUrl: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookDetailsReadingActivity = ActivityBookDetailsReadingBinding.inflate(layoutInflater)
        setContentView(bookDetailsReadingActivity.root)

        setSupportActionBar(bookDetailsReadingActivity.toolbarBookDetailsReading)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        booksUrl = intent.getStringExtra(BOOKS_URL).toString()

        val webSettings: WebSettings = bookDetailsReadingActivity.webView.settings
        webSettings.javaScriptEnabled = true
        bookDetailsReadingActivity.webView.loadUrl(booksUrl)
        bookDetailsReadingActivity.webView.webViewClient = WebViewClient()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (bookDetailsReadingActivity.webView.canGoBack()) {
            bookDetailsReadingActivity.webView.goBack()
        } else {
            finish()
            startActivity(Intent(this, BookReadingActivity::class.java))
        }
    }

}