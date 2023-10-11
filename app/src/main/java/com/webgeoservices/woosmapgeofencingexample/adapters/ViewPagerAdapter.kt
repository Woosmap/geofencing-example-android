package com.webgeoservices.woosmapgeofencingexample.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.webgeoservices.woosmapgeofencingexample.fragments.EventFragment
import com.webgeoservices.woosmapgeofencingexample.fragments.LocationFragment

/***
 * Populates ViewPager with `LocationFragment` and `EventFragment`
 */
class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    val locationFragment = LocationFragment()
    val eventFragment = EventFragment()
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> locationFragment
            1 -> eventFragment
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun clearLists(){
        locationFragment.clearList()
        eventFragment.clearList()
    }

}