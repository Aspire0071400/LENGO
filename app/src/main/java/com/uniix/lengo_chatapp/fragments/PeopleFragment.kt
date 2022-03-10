package com.uniix.lengo_chatapp.fragments

import android.arch.paging.PagedList
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.FragmentChatsBinding
import com.uniix.lengo_chatapp.interaction.ChatActivity
import com.uniix.lengo_chatapp.models.User
import com.uniix.lengo_chatapp.viewholders.EmptyViewHolder
import com.uniix.lengo_chatapp.viewholders.UsersViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions



private const val DELETED_VIEW_TYPE = 1
private const val NORMAL_VIEW_TYPE = 2

class PeopleFragment:Fragment() {

    private lateinit var peopleFragment: FragmentChatsBinding
    private lateinit var mAdapter: FirestorePagingAdapter<User, ViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val database = FirebaseFirestore.getInstance().collection("users")
        .orderBy("name", Query.Direction.ASCENDING)
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        peopleFragment = FragmentChatsBinding.inflate(inflater)

        viewManager = LinearLayoutManager(requireContext())
        setupAdapter()

        return peopleFragment.root
    }

    private fun setupAdapter() {
        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(database, config, User::class.java)
            .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<User, ViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder {
                val inflater = layoutInflater
                return when (viewType) {
                    NORMAL_VIEW_TYPE -> {
                        UsersViewHolder(inflater.inflate(R.layout.list_item, parent, false))
                    }
                    else -> EmptyViewHolder(inflater.inflate(R.layout.empty_view, parent, false))
                }
            }

            override fun onBindViewHolder(
                viewHolder: ViewHolder,
                position: Int,
                user: User
            ) {
                // Bind to ViewHolder
                if (viewHolder is UsersViewHolder) {
                    if (auth.uid == user.uid) {
                        currentList?.snapshot()?.removeAt(position)
                        notifyItemRemoved(position)
                    } else {
                        viewHolder.bind(user) { name: String, photo: String, id: String, _: Context ->

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

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("MainActivity", e.message.toString())
            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return if (auth.uid == item!!.uid) {
                    DELETED_VIEW_TYPE
                } else {
                    NORMAL_VIEW_TYPE
                }
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                    }
                    LoadingState.LOADING_MORE -> {
                    }
                    LoadingState.LOADED -> {
                    }
                    LoadingState.ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            "Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    LoadingState.FINISHED -> {
                    }
                }
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        peopleFragment.recyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter
        }
    }

}

