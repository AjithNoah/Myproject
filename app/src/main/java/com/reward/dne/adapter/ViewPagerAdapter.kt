package com.reward.dne.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.reward.dne.fragment.CompletedFragment
import com.reward.dne.fragment.OfferFragment
import com.reward.dne.fragment.PendingFragment

class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    private val COUNT = 3

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = OfferFragment()
            1 -> fragment = PendingFragment()
            2 -> fragment = CompletedFragment()
        }
        return fragment

    }

    override fun getCount(): Int {
        return COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title:String? = null
        when (position) {
            0 -> title = "Offers"
            1 -> title = "Pending"
            2 -> title = "Completed"
        }
        return title
    }


}