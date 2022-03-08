package com.uniix.lengo_chatapp.books

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ItemBookBinding
import java.util.*

class BookAdapter(
    private val bookInfoArrayList: ArrayList<BookInfo>, val context: Context
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bookAdapter = ItemBookBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.BookViewHolder {
        val adapter = ItemBookBinding.inflate(LayoutInflater.from(parent.context))
        return BookViewHolder(adapter.root)
    }

    override fun onBindViewHolder(holder: BookAdapter.BookViewHolder, position: Int) {
        val colors = context.resources.getIntArray(R.array.random_color)
        val randomColor = colors[Random().nextInt(colors.size)]
        holder.bookAdapter.viewColorTagBook.setBackgroundColor(randomColor)
        holder.bookAdapter.bookTitle.text = bookInfoArrayList[position].title
        holder.bookAdapter.publisher.text = bookInfoArrayList[position].publisher
        holder.bookAdapter.pageCount.text = "No. of Pages: ${bookInfoArrayList[position].pageCount}"
        holder.bookAdapter.date.text = bookInfoArrayList[position].publishedDate
        Picasso.get().load(bookInfoArrayList[position].thumbnail).into(holder.bookAdapter.book)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetailsActivity::class.java)
            intent.putExtra("title", bookInfoArrayList[position].title)
            intent.putExtra("subtitle", bookInfoArrayList[position].subtitle)
            intent.putExtra("publisher", bookInfoArrayList[position].publisher)
            intent.putExtra("publishedDate", bookInfoArrayList[position].publishedDate)
            intent.putExtra("description", bookInfoArrayList[position].description)
            intent.putExtra("pageCount", bookInfoArrayList[position].pageCount)
            intent.putExtra("thumbnail", bookInfoArrayList[position].thumbnail)
            intent.putExtra("previewLink", bookInfoArrayList[position].previewLink)
            intent.putExtra("infoLink", bookInfoArrayList[position].infoLink)
            intent.putExtra("buyLink", bookInfoArrayList[position].buyLink)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookInfoArrayList.size
    }

}