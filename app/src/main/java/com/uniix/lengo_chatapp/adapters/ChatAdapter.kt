package com.uniix.lengo_chatapp.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.models.ChatEvent
import com.uniix.lengo_chatapp.models.DateHeader
import com.uniix.lengo_chatapp.models.Message
import com.uniix.lengo_chatapp.utils.formatAsTime
import kotlinx.android.synthetic.main.list_item_chat_recv_message.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

class ChatAdapter(
    private val list: MutableList<ChatEvent>,
    private val mCurrentUser: String,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var highFiveClick: ((id: String, status: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layout: Int ->
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        }
        return when (viewType) {
            TEXT_MESSAGE_RECEIVED -> {
                MessageHolder(
                    inflate(R.layout.list_item_chat_recv_message)
                )
            }
            TEXT_MESSAGE_SENT -> {
                MessageHolder(
                    inflate(R.layout.list_item_chat_sent_message)
                )
            }
            DATE_HEADER -> {
                DateHeaderHolder(
                    inflate(R.layout.list_item_date_header)
                )
            }
            else -> {
                MessageHolder(
                    inflate(R.layout.list_item_chat_recv_message)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val event = list[position]) {
            is Message -> {
                if (event.senderId == mCurrentUser) {
                    TEXT_MESSAGE_SENT
                } else {
                    TEXT_MESSAGE_RECEIVED
                }
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    override fun getItemCount(): Int= list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = list[position]) {
            is DateHeader -> {
                holder.itemView.textView.text = item.date
            }
            is Message -> {
                holder.itemView.content.text = item.msg
                holder.itemView.time.text = item.sentAt.formatAsTime()
                when (getItemViewType(position)) {
                    TEXT_MESSAGE_RECEIVED -> {
                        holder.itemView.messageCardView.setOnClickListener(object :
                            DoubleClickListener() {
                            override fun onDoubleClick(v: View?) {
                                highFiveClick?.invoke(item.msgId, !item.liked)
                            }
                        })
                        holder.itemView.messageCardView.setOnLongClickListener {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                    as ClipboardManager
                            val clipData = ClipData.newPlainText(
                                "text", holder.itemView.messageCardView.content.text.toString()
                            )
                            clipboard.setPrimaryClip(clipData)
                            Toast.makeText(
                                context,
                                "Text copied to clipboard",
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        }
                        holder.itemView.highFiveImg.apply {
                            isVisible = position == itemCount - 1 || item.liked
                            isSelected = item.liked
                            setOnClickListener {
                                highFiveClick?.invoke(item.msgId, !isSelected)
                            }
                        }
                    }
                    TEXT_MESSAGE_SENT -> {
                        holder.itemView.setOnLongClickListener {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                    as ClipboardManager
                            val clipData = ClipData.newPlainText(
                                "text", holder.itemView.content.text.toString()
                            )
                            clipboard.setPrimaryClip(clipData)
                            Toast.makeText(
                                context,
                                "Text copied to clipboard",
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        }
                        holder.itemView.highFiveImg.apply {
                            isVisible = item.liked
                        }
                    }
                }
            }
        }
    }

    class DateHeaderHolder(view: View) : RecyclerView.ViewHolder(view)

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }
}

abstract class DoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}