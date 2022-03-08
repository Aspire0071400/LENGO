package com.uniix.lengo_chatapp.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniix.lengo_chatapp.MainActivity
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ActivityNewsReadingBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NewsReadingActivity : AppCompatActivity(), CategoryAdapter.CategoryClickInterface {

    // Initialising Variables
    private lateinit var newsReadingActivity: ActivityNewsReadingBinding
    private var articlesArrayList = arrayListOf<Articles>()
    private var categoryModelArrayList = arrayListOf<CategoryModel>()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsReadingActivity = ActivityNewsReadingBinding.inflate(layoutInflater)
        setContentView(newsReadingActivity.root)

        setSupportActionBar(newsReadingActivity.toolbarNews)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        getNews("All")
        getCategories()

    }

    private fun getCategories() {
        categoryModelArrayList.add(
            CategoryModel(
                "All",
                "https://media.istockphoto.com/photos/colorful-abstract-collage-from-clippings-with-letters-and-numbers-picture-id1278583120?s=612x612"
            )
        )
        categoryModelArrayList.add(
            CategoryModel(
                "Technology",
                "https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=2072&q=80"
            )
        )
        categoryModelArrayList.add(
            CategoryModel(
                "Science",
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1172&q=80"
            )
        )
        categoryModelArrayList.add(
            CategoryModel(
                "Sports",
                "https://media.istockphoto.com/photos/various-sport-equipments-on-grass-picture-id949190736?b=1&k=20&m=949190736&s=170667a&w=0&h=f3ofVqhbmg2XSVOa3dqmvGtHc4VLA_rtbboRGaC8eNo="
            )
        )
        categoryModelArrayList.add(
            CategoryModel(
                "General",
                "https://images.unsplash.com/photo-1557822477-ec8d8bbbcd2a?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MTl8fGdlbmVyYWwlMjBuZXdzfGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=60"
            )
        )
        categoryModelArrayList.add(
            CategoryModel(
                "Business",
                "https://media.istockphoto.com/photos/click-here-and-it-should-direct-you-further-picture-id1209869264?b=1&k=20&m=1209869264&s=170667a&w=0&h=dGbPJ8Beo5oTkIEg6Yxh8bX057_lkx4L7qqTbs7dzsA="
            )
        )
        categoryModelArrayList.add(
            CategoryModel(
                "Entertainment",
                "https://media.istockphoto.com/photos/the-musicians-were-playing-rock-music-on-stage-there-was-an-audience-picture-id1319479588?b=1&k=20&m=1319479588&s=170667a&w=0&h=bunblYyTDA_vnXu-nY4x4oa7ke6aiiZKntZ5mfr-4aM="
            )
        )
        categoryModelArrayList.add(
            CategoryModel(
                "Health",
                "https://images.unsplash.com/photo-1498837167922-ddd27525d352?ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8aGVhbHRofGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=60"
            )
        )
        newsReadingActivity.categories.adapter = CategoryAdapter(
            categoryModelArrayList,
            this,
            object : CategoryAdapter.CategoryClickInterface {
                override fun onCategoryClick(position: Int) {
                    val category = categoryModelArrayList[position].category
                    getNews(category)
                }
            })
    }

    private fun getNews(category: String) {
        articlesArrayList.clear()
        newsReadingActivity.progressBarNews.visibility = View.VISIBLE
        val url = if (category != "All") {
            "https://newsapi.org/v2/top-headlines?country=in&category=$category&apiKey=ENTER_YOUR_API_KEY"

        } else {
            "https://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=published&language=en&apiKey=ENTER_YOUR_API_KEY"
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.cache.clear()
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Request.Method.GET, url, null, {
                newsAdapter = NewsAdapter(articlesArrayList, this)
                var jsonArray: JSONArray? = null
                try {
                    jsonArray = it.getJSONArray("articles")
                    var articles: Articles =
                        Articles(
                            jsonArray.getJSONObject(0).get("description").toString(),
                            jsonArray.getJSONObject(0).get("title").toString(),
                            jsonArray.getJSONObject(0).get("content").toString(),
                            jsonArray.getJSONObject(0).get("urlToImage").toString(),
                            jsonArray.getJSONObject(0).get("url").toString()
                        )
                    newsReadingActivity.progressBarNews.visibility = View.GONE
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        val newsModel: Articles =
                            Articles(
                                jsonObject.get("title").toString(),
                                jsonObject.get("description").toString(),
                                jsonObject.get("content").toString(),
                                jsonObject.get("urlToImage").toString(),
                                jsonObject.get("url").toString()
                            )
                        articlesArrayList.add(newsModel)
                    }
                    newsReadingActivity.news.adapter = NewsAdapter(articlesArrayList, this)
                } catch (e: JSONException) {
                    //e.printStackTrace()
                    Toast.makeText(
                        this@NewsReadingActivity,
                        "Failed to load News!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, {
                /*if( it.networkResponse.statusCode == 403) {
                    getNews(category)
                }*/
                Toast.makeText(
                    this@NewsReadingActivity,
                    "Failed to load News!!",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    return headers
                }
            }
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
        articlesArrayList.clear()
        finish()
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    override fun onCategoryClick(position: Int) {
        val category = categoryModelArrayList[position].category
        getNews(category)
    }

}