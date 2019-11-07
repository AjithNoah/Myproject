package com.reward.dne.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.reward.dne.R
import com.reward.dne.activity.AppDetailActivity
import com.reward.dne.application.RewardApplication
import com.reward.dne.retrofit.RewardAPI
import kotlinx.android.synthetic.main.activity_app_detail.*
import javax.inject.Inject


class FormFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var app_id:Int = 0
    var appName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RewardApplication.instance.getComponent().inject(this)

        val args = arguments
        app_id = args?.getInt("app_id")!!
        appName = args.getString("app_name")!!
       // (activity as AppDetailActivity).headerAppDetail.text =  appName
    }

}
