package com.reward.dne.fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide

import com.reward.dne.R
import com.reward.dne.activity.AppDetailActivity
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.InputParams
import com.reward.dne.model.OfferDetailResponse
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.CustomTextViewBold
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_install_app.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

class InstallAppFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var app_id:Int = 0
    var appName = ""
    var offerDetailResponse:OfferDetailResponse? = null
    var storeLink = ""
    lateinit var textAppName:CustomTextViewBold

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_install_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RewardApplication.instance.getComponent().inject(this)
        textAppName = view.findViewById(R.id.textAppName)
        val args = arguments
        app_id = args?.getInt("app_id")!!
        appName = args.getString("app_name")!!

        relInstall.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(storeLink)))
        }
    }

}
