package com.reward.dne.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import com.reward.dne.adapter.ViewPagerRedeem
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_redeem.*


class RedeemFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redeem, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.redeem)

        val adapter = ViewPagerRedeem(childFragmentManager) 
        viewPagerRedeem.adapter = adapter
        tabLayoutRedeem.setupWithViewPager(viewPagerRedeem)

        viewPagerRedeem.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0->{ mainBack = 1 }
                    1->{ mainBack = 1}
                }
            }

        })
    }



}
