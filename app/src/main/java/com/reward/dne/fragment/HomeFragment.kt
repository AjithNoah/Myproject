package com.reward.dne.fragment


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.reward.dne.R
import com.reward.dne.adapter.ViewPagerAdapter
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import kotlinx.android.synthetic.main.app_bar_main.*


class HomeFragment : BaseFragment() {

    private val tabIcons = intArrayOf(R.mipmap.gift, R.mipmap.clock, R.mipmap.check)
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val adapter = ViewPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        setupTabIcons()
        tabLayout.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                super.onTabSelected(tab)
                val tabIconColor = ContextCompat.getColor(activity!!, R.color.colorPrimary)
                tab!!.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                super.onTabReselected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                super.onTabUnselected(tab)
                val tabIconColor = ContextCompat.getColor(activity!!, R.color.lightgrey)
                tab!!.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }

        })
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0->{ mainBack = 0 }
                    1->{ mainBack = 1}
                    2->{ mainBack = 1}
                }
            }

        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       mainBack = 0
        (activity as MainActivity).textEditProfile.visibility = View.GONE
        (activity as MainActivity).relativeWalletAmount.visibility = View.VISIBLE
        (activity as MainActivity).headerName.text = getString(R.string.home)
    }

    private fun setupTabIcons() {
        tabLayout.getTabAt(0)!!.setIcon(tabIcons[0])
        tabLayout.getTabAt(1)!!.setIcon(tabIcons[1])
        tabLayout.getTabAt(2)!!.setIcon(tabIcons[2])
    }

}
