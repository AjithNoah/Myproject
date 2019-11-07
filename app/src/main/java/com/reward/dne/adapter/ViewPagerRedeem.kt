package com.reward.dne.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import com.reward.dne.fragment.RedeemHistoryFragment
import com.reward.dne.fragment.RedeemNowFragment

class ViewPagerRedeem (fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    private val COUNT = 2

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = RedeemNowFragment()
            1 -> fragment = RedeemHistoryFragment()
        }
        return fragment

    }

    override fun getCount(): Int {
        return COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title:String? = null
        when (position) {
            0 -> title = "Redeem Now"
            1 -> title = "Redeem History"
        }
        return title
    }
}