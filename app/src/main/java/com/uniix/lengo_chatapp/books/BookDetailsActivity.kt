package com.uniix.lengo_chatapp.books

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ActivityBookDetailsBinding


const val TITLE = "title"
const val SUBTITLE = "subtitle"
const val PUBLISHER = "publisher"
const val PUBLISHED_DATE = "publishedDate"
const val DESCRIPTION = "description"
const val PAGE_COUNT = "pageCount"
const val THUMBNAIL = "thumbnail"
const val PREVIEW_LINK = "previewLink"
const val INFO_LINK = "infoLink"
const val BUY_LINK = "buyLink"

class BookDetailsActivity : AppCompatActivity() {

    // Initializing Variables
    private lateinit var bookDetailsActivity: ActivityBookDetailsBinding
    private lateinit var title: String
    private lateinit var subtitle: String
    private lateinit var publisher: String
    private lateinit var publishedDate: String
    private lateinit var description: String
    private lateinit var pageCount: String
    private lateinit var thumbnail: String
    private lateinit var previewLink: String
    private lateinit var infoLink: String
    private lateinit var buyLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookDetailsActivity = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(bookDetailsActivity.root)

        setSupportActionBar(bookDetailsActivity.toolbarBooksDetails)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        title = intent.getStringExtra(TITLE).toString()
        subtitle = intent.getStringExtra(SUBTITLE).toString()
        publisher = intent.getStringExtra(PUBLISHER).toString()
        publishedDate = intent.getStringExtra(PUBLISHED_DATE).toString()
        description = intent.getStringExtra(DESCRIPTION).toString()
        pageCount = intent.getStringExtra(PAGE_COUNT).toString()
        thumbnail = intent.getStringExtra(THUMBNAIL).toString()
        previewLink = intent.getStringExtra(PREVIEW_LINK).toString()
        infoLink = intent.getStringExtra(INFO_LINK).toString()
        buyLink = intent.getStringExtra(BUY_LINK).toString()

        val delim = ":"
        val arr = previewLink.split(delim).toTypedArray()
        arr[0] = "https:"
        val bookPreviewLink = "${arr[0]}${arr[1]}"

        bookDetailsActivity.bookTitle.text = title
        bookDetailsActivity.bookSubTitle.text = subtitle
        bookDetailsActivity.publishers.text = publisher
        bookDetailsActivity.publishDate.text = "Published on: $publishedDate"
        bookDetailsActivity.bookDescription.text = description
        bookDetailsActivity.noOfPages.text = "No. of Pages: $pageCount"
        Picasso.get().load(thumbnail).into(bookDetailsActivity.books)

        bookDetailsActivity.previewBtn.setOnClickListener {
            if (previewLink.isEmpty()) {
                Toast.makeText(this, "No preview Link present!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                openBookPreview(bookPreviewLink, it)
            }
        }

        bookDetailsActivity.buyBtn.setOnClickListener {
            if (buyLink.isEmpty()) {
                Toast.makeText(
                    this,
                    "No buy page present for this book!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val uri: Uri = Uri.parse(buyLink)
                val i = Intent(Intent.ACTION_VIEW, uri)
                startActivity(i)
            }
        }

    }

    private fun openBookPreview(bookPreviewLink: String, view: View) {
        //Instantiate builder variable
        val builder = AlertDialog.Builder(view.context)
        // set title
        builder.setTitle("LENGO")
        //set content area
        builder.setMessage("Want to preview the book in: -")
        //set negative button
        builder.setPositiveButton(
            "Browser"
        ) { _, _ ->
            val uri = Uri.parse(bookPreviewLink)
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }
        //set positive button
        builder.setNegativeButton(
            "App"
        ) { _, _ ->
            startActivity(
                Intent(this, BookDetailsReadingActivity::class.java)
                    .putExtra("URL", bookPreviewLink)
            )
        }
        //set neutral button
        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, BookReadingActivity::class.java))
    }

}