package com.uniix.lengo_chatapp.news

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.databinding.ItemNewsBinding

class NewsAdapter(
    private val articlesArrayList: ArrayList<Articles>,
    val context: Context
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var newsAdapter = ItemNewsBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        val adapter = ItemNewsBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(adapter.root)
    }

    override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {
        /*val colors = context.resources.getIntArray(R.array.random_color)
        val randomColor = colors[Random().nextInt(colors.size)]
        holder.newsAdapter.viewColorTagNews.setBackgroundColor(randomColor)*/
        holder.newsAdapter.newsHeading.text = articlesArrayList[position].title
        holder.newsAdapter.newsSubHeading.text = articlesArrayList[position].description
        Picasso.get()
            .load(articlesArrayList[position].urlToImage)
            .into(holder.newsAdapter.newsImage)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, NewsDetailsActivity::class.java)
            intent.putExtra("title", articlesArrayList[position].title)
            intent.putExtra("content", articlesArrayList[position].content)
            intent.putExtra("description", articlesArrayList[position].description)
            intent.putExtra("image", articlesArrayList[position].urlToImage)
            intent.putExtra("url", articlesArrayList[position].url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articlesArrayList.size
    }

}