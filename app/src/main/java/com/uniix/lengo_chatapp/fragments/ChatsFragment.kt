package com.uniix.lengo_chatapp.fragments

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.FragmentChatsBinding
import com.uniix.lengo_chatapp.interaction.ChatActivity
import com.uniix.lengo_chatapp.models.Inbox
import com.uniix.lengo_chatapp.viewholders.ChatViewHolder
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment() {

    private lateinit var chatsFragment: FragmentChatsBinding
    private lateinit var mAdapter: FirebaseRecyclerAdapter<Inbox, ChatViewHolder>

    //private lateinit var viewManager: RecyclerView.LayoutManager
    private val mLinearLayout: LinearLayoutManager by lazy { LinearLayoutManager(requireContext()) }
    private val mDatabase = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatsFragment = FragmentChatsBinding.inflate(inflater)

        //viewManager = LinearLayoutManager(requireContext()/*, LinearLayoutManager.VERTICAL, true*/)
        mLinearLayout.reverseLayout = true
        mLinearLayout.stackFromEnd = true
        setupAdapter()

        return chatsFragment.root
    }

    private fun setupAdapter() {

        val baseQuery: Query =
            mDatabase.reference.child("chats").child(auth.uid!!)

        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery.orderByChild("time/time"), Inbox::class.java)
            .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirebaseRecyclerAdapter<Inbox, ChatViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val inflater = layoutInflater
                return ChatViewHolder(inflater.inflate(R.layout.list_item, parent, false))
            }

            override fun onBindViewHolder(
                viewHolder: ChatViewHolder,
                position: Int,
                inbox: Inbox
            ) {
                viewHolder.bind(inbox) { name: String, photo: String, id: String, _: Context ->
                    startActivity(
                        ChatActivity.createChatActivity(
                            requireContext(),
                            id,
                            name,
                            photo
                        )
                    )
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            layoutManager = mLinearLayout
            adapter = mAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }
}