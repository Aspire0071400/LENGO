package com.uniix.lengo_chatapp.viewholders

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.interaction.OthersProfileActivity
import com.uniix.lengo_chatapp.models.User
import kotlinx.android.synthetic.main.list_item.view.*

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        user: User,
        onClick: (name: String, photo: String, id: String, context: Context) -> Unit
    ) =
        with(itemView) {
            countTv.isVisible = false
            timeTv.isVisible = false
            titleTv.text = user.name
            subTitleTv.text = user.status
            Picasso.get()
                .load(user.thumbImage)
                .placeholder(R.drawable.defaulavatar)
                .error(R.drawable.defaulavatar)
                .into(userImgView)
            userImgView.setOnClickListener {
                //onClick.invoke(user.name, user.thumbImage, user.uid)
                context.startActivity(
                    OthersProfileActivity.createOthersProfileActivity(
                        context, user.uid
                    )
                )
            }
            setOnClickListener {
                onClick.invoke(user.name, user.thumbImage, user.uid, context)
            }
        }
}

class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

