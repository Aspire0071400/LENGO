package com.uniix.lengo_chatapp.books

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniix.lengo_chatapp.MainActivity
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ActivityBookReadingBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BookReadingActivity : AppCompatActivity() {

    // Initializing Variables
    private lateinit var bookReadingActivity: ActivityBookReadingBinding
    private var bookInfoArrayList = ArrayList<BookInfo>()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookReadingActivity = ActivityBookReadingBinding.inflate(layoutInflater)
        setContentView(bookReadingActivity.root)

        setSupportActionBar(bookReadingActivity.toolbarBooks)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        getBooksInfo("android")

        bookReadingActivity.searchBtn.setOnClickListener {
            if (bookReadingActivity.searchBooks.text.toString().isEmpty()) {
                bookReadingActivity.searchBooks.error = "Please enter search query!!"
            } else {
                getBooksInfo(bookReadingActivity.searchBooks.text.toString())
            }
        }

    }

    private fun getBooksInfo(query: String) {
        bookInfoArrayList.clear()
        bookReadingActivity.progressBarBooks.visibility = View.VISIBLE
        val url = if (query == "android") {
            "https://www.googleapis.com/books/v1/volumes?q=android&key=ENTER_YOUR_API_KEY"
        } else {
            "https://www.googleapis.com/books/v1/volumes?q=$query&key=ENTER_YOUR_API_KEY"
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.cache.clear()
        val jsonObjectRequest: JsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, url, null, {
                bookAdapter = BookAdapter(bookInfoArrayList, this)
                var jsonArray: JSONArray? = null
                bookReadingActivity.progressBarBooks.visibility = View.GONE
                try {
                    jsonArray = it.getJSONArray("items")
                    for (i in 0 until jsonArray.length()) {
                        val itemsObj: JSONObject = jsonArray.getJSONObject(i)
                        val volumeObj = itemsObj.getJSONObject("volumeInfo")
                        val title = volumeObj.optString("title")
                        val subtitle = volumeObj.optString("subtitle")
                        val authorsArray = volumeObj.getJSONArray("authors")
                        val publisher = volumeObj.optString("publisher")
                        val publishedDate = volumeObj.optString("publishedDate")
                        val description = volumeObj.optString("description")
                        val pageCount: String = volumeObj.optString("pageCount")
                        val imageLinks = volumeObj.optJSONObject("imageLinks")
                        val thumbnail = imageLinks.optString("thumbnail")
                        val previewLink = volumeObj.optString("previewLink")
                        val infoLink = volumeObj.optString("infoLink")
                        val saleInfoObj = itemsObj.optJSONObject("saleInfo")
                        val buyLink = saleInfoObj.optString("buyLink")
                        val authorsArrayList: ArrayList<String> = ArrayList()
                        if (authorsArray.length() != 0) {
                            for (j in 0 until authorsArray.length()) {
                                authorsArrayList.add(authorsArray.optString(i))
                            }
                        }
                        val delim = ":"
                        val arr = thumbnail.split(delim).toTypedArray()
                        arr[0] = "https:"
                        val bookThumbnail = "${arr[0]}${arr[1]}"
                        val bookInfo = BookInfo(
                            title,
                            subtitle,
                            authorsArrayList,
                            publisher,
                            publishedDate,
                            description,
                            pageCount,
                            bookThumbnail,
                            previewLink,
                            infoLink,
                            buyLink
                        )
                        bookInfoArrayList.add(bookInfo)
                        val adapter = BookAdapter(bookInfoArrayList, this)
                        val linearLayoutManager =
                            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        bookReadingActivity.booksList.layoutManager = linearLayoutManager
                        bookReadingActivity.booksList.adapter = adapter
                    }
                    bookReadingActivity.booksList.adapter = BookAdapter(bookInfoArrayList, this)
                } catch (e: JSONException) {
                    //e.printStackTrace()
                    Toast.makeText(this, "Failed to load Books!!", Toast.LENGTH_SHORT).show()
                }
            }, {
                /*if( it.networkResponse.statusCode == 403) {
                    getNews(category)
                }*/
                Toast.makeText(this, "Failed to load Books!!", Toast.LENGTH_SHORT).show()
            })
        requestQueue.add(jsonObjectRequest)
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
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

}
