package com.uniix.lengo_chatapp.interaction

import android.content.Context
import android.content.Intent
import android.os.Build.ID
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.MainActivity
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.adapters.ChatAdapter
import com.uniix.lengo_chatapp.databinding.ActivityChatBinding
import com.uniix.lengo_chatapp.models.*
import com.uniix.lengo_chatapp.utils.KeyboardVisibilityUtil
import com.uniix.lengo_chatapp.utils.isSameDayAs
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val USER_ID = "userId"
const val USER_NAME = "userName"
const val USER_THUMB_IMAGE = "thumbImage"

class ChatActivity : AppCompatActivity() {

    private lateinit var chatActivity: ActivityChatBinding
    private lateinit var friendId: String
    private lateinit var name: String
    private lateinit var image: String
    private val mCurrentUid: String = FirebaseAuth.getInstance().uid!!
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var currentUser: User
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val mutableItems: MutableList<ChatEvent> = mutableListOf()
    private val mLinearLayout: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        chatActivity = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatActivity.root)

        setSupportActionBar(chatActivity.toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        friendId = intent.getStringExtra(USER_ID).toString()
        name = intent.getStringExtra(USER_NAME).toString()
        image = intent.getStringExtra(USER_THUMB_IMAGE).toString()

        keyboardVisibilityHelper = KeyboardVisibilityUtil(rootView) {
            msgRv.scrollToPosition(mutableItems.size - 1)
        }

        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
            .addOnSuccessListener {
                currentUser = it.toObject(User::class.java)!!
            }

        chatAdapter = ChatAdapter(mutableItems, mCurrentUid, this)

        mLinearLayout.reverseLayout = false
        mLinearLayout.stackFromEnd = true
        msgRv.apply {
            layoutManager = mLinearLayout
            adapter = chatAdapter
        }

        nameTv.text = name
        Picasso.get().load(image).into(userImgView)

        nameTv.setOnClickListener {
            viewProfile()
        }

        userImgView.setOnClickListener {
            viewProfile()
        }

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener {
            emojiPopup.toggle()
        }

        swipeToLoad.setOnRefreshListener {
            val workerScope = CoroutineScope(Dispatchers.Main)
            workerScope.launch {
                delay(2000)
                swipeToLoad.isRefreshing = false
            }
        }

        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }

        listenMessages() { msg, update ->
            if (update) {
                updateMessage(msg)
                updateReadCount()
            } else {
                addMessage(msg)
                updateReadCount()
            }
        }

        chatAdapter.highFiveClick = { id, status ->
            updateHighFive(id, status)
        }

        userStatus()

    }

    private fun userStatus() {
        val statusListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val status =
                    snapshot.child("status").child(friendId).child("status").value.toString()
                statusTv.text = status
                /*val seen =
                    snapshot.child("chats/$friendId/$mCurrentUid").child("count").value.toString()
                val seenStatus = snapshot.child("chats/$mCurrentUid/$friendId").child("deliveryStatus").value.toString()
                when {
                    status == "Online" -> {
                        getInbox(friendId, mCurrentUid).child("deliveryStatus").setValue("Sent")
                    }
                    status == "OffLine" -> {
                        getInbox(friendId, mCurrentUid).child("deliveryStatus")
                            .setValue("Delivered")
                    }
                    seen == "0" -> {
                        getInbox(friendId, mCurrentUid).child("deliveryStatus").setValue("Seen")
                    }
                }*/
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
            }
        }
        dbRef.addValueEventListener(statusListener)
    }

    private fun viewProfile() {
        startActivity(
            Intent(this, OthersProfileActivity::class.java)
                .putExtra(ID, friendId)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onBackPressed() {
        finish()
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun updateReadCount() {
        getInbox(mCurrentUid, friendId).child("count").setValue(0)
    }

    private fun updateHighFive(id: String, status: Boolean) {
        getMessages(friendId).child(id).updateChildren(mapOf("liked" to status))
    }

    private fun addMessage(event: Message) {
        val eventBefore = mutableItems.lastOrNull()
        // Add date header if it's a different day
        if ((eventBefore != null
                    && !eventBefore.sentAt.isSameDayAs(event.sentAt))
            || eventBefore == null
        ) {
            mutableItems.add(
                DateHeader(
                    event.sentAt, this
                )
            )
        }
        mutableItems.add(event)
        chatAdapter.notifyItemInserted(mutableItems.size)
        msgRv.smoothScrollToPosition(mutableItems.size + 1)
    }

    private fun updateMessage(msg: Message) {
        val position = mutableItems.indexOfFirst {
            when (it) {
                is Message -> it.msgId == msg.msgId
                else -> false
            }
        }
        mutableItems[position] = msg
        chatAdapter.notifyItemChanged(position)
    }

    private fun listenMessages(newMsg: (msg: Message, update: Boolean) -> Unit) {
        getMessages(friendId)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(data: DataSnapshot, p1: String?) {
                    val msg = data.getValue(Message::class.java)!!
                    newMsg(msg, true)
                }

                override fun onChildAdded(data: DataSnapshot, p1: String?) {
                    val msg = data.getValue(Message::class.java)!!
                    newMsg(msg, false)
                }

                override fun onChildRemoved(p0: DataSnapshot) {}
            })
    }

    private fun sendMessage(msg: String) {
        val id = getMessages(friendId).push().key
        checkNotNull(id) { "Cannot be null" }
        val msgMap = Message(msg, mCurrentUid, id)
        getMessages(friendId).child(id).setValue(msgMap).addOnSuccessListener {
            Log.i("Chats", "Success")
        }.addOnFailureListener {
            Log.i("Chats", it.localizedMessage)
        }
        updateLastMessage(msgMap, mCurrentUid)
    }

    private fun updateLastMessage(message: Message, mCurrentUid: String) {
        val inboxMap = Inbox(
            message.msg,
            friendId,
            name,
            image,
            message.sentAt,
            0
        )

        getInbox(mCurrentUid, friendId).setValue(inboxMap).addOnSuccessListener {
            getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)
                    inboxMap.apply {
                        from = message.senderId
                        name = currentUser.name
                        image = currentUser.thumbImage
                        count = 1
                    }
                    if (value?.from == message.senderId) {
                        inboxMap.count = value.count + 1
                    }
                    getInbox(friendId, mCurrentUid).setValue(inboxMap)
                }
            })
        }

        /*getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.getValue(Inbox::class.java)
                inboxMap.apply {
                    from = message.senderId
                    name = currentUser.name
                    image = currentUser.thumbImage
                    count = 1
                }
                if (value?.from == message.senderId) {
                    inboxMap.count = value.count + 1
                }
                getInbox(friendId, mCurrentUid).setValue(inboxMap)
            }
        })*/
    }

    private fun markAsRead() {
        getInbox(friendId, mCurrentUid).child("count").setValue(0)
    }

    private fun getMessages(friendId: String) =
        db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
        db.reference.child("chats/$toUser/$fromUser")

    private fun getId(friendId: String): String {
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }

    companion object {
        fun createChatActivity(context: Context, id: String, name: String, image: String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(USER_ID, id)
            intent.putExtra(USER_NAME, name)
            intent.putExtra(USER_THUMB_IMAGE, image)
            return intent
        }
    }
}