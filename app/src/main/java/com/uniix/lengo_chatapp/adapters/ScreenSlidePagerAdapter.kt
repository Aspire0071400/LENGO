package com.uniix.lengo_chatapp.adapters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.uniix.lengo_chatapp.fragments.ChatsFragment
import com.uniix.lengo_chatapp.fragments.PeopleFragment

class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val numPages: Int = 2
    override fun getItemCount(): Int =numPages

    override fun createFragment(position: Int): Fragment = when (position){
        0-> ChatsFragment()
        else-> PeopleFragment()
    }


}