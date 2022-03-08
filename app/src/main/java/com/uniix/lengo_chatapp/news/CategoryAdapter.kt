package com.uniix.lengo_chatapp.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.databinding.ItemCategoriesBinding

class CategoryAdapter(
    private val categoryAdapterArrayList: ArrayList<CategoryModel>,
    val context: Context,
    private val categoryClickInterface: CategoryClickInterface
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryAdapter = ItemCategoriesBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val adapter = ItemCategoriesBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(adapter.root)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        holder.categoryAdapter.categoryTitle.text = categoryAdapterArrayList[position].category
        Picasso.get()
            .load(categoryAdapterArrayList[position].categoryImageUrl)
            .into(holder.categoryAdapter.categoryImage)
        holder.itemView.setOnClickListener {
            categoryClickInterface.onCategoryClick(position)
        }
    }

    override fun getItemCount(): Int {
        return categoryAdapterArrayList.size
    }

    interface CategoryClickInterface {
        fun onCategoryClick(position: Int)
    }

}