package com.uniix.lengo_chatapp.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ActivityNewsDetailsBinding

const val TITLE = "title"
const val CONTENT = "content"
const val DESCRIPTION = "description"
const val IMAGE = "image"
const val URL = "url"

class NewsDetailsActivity : AppCompatActivity() {

    // Initializing Variables
    private lateinit var newsDetailsActivity: ActivityNewsDetailsBinding
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var description: String
    private lateinit var image: String
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsDetailsActivity = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(newsDetailsActivity.root)

        setSupportActionBar(newsDetailsActivity.toolbarNewsDetails)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        title = intent.getStringExtra(TITLE).toString()
        content = intent.getStringExtra(CONTENT).toString()
        description = intent.getStringExtra(DESCRIPTION).toString()
        image = intent.getStringExtra(IMAGE).toString()
        url = intent.getStringExtra(URL).toString()

        val delim = "["
        val arr = content.split(delim).toTypedArray()
        val newsContent = arr[0] + "\nCLICK THE BUTTON BELOW TO READ FULL ARTICLE"

        newsDetailsActivity.newsDetailTitle.text = title
        newsDetailsActivity.newsDetailDescription.text = description
        newsDetailsActivity.newsDetailContent.text = newsContent
        Picasso.get().load(image).into(newsDetailsActivity.newsDetailImage)

        newsDetailsActivity.readFullNews.setOnClickListener {
            startActivity(
                Intent(this, NewsDetailsReadingActivity::class.java)
                    .putExtra("URL", url)
            )
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, NewsReadingActivity::class.java))
    }

}