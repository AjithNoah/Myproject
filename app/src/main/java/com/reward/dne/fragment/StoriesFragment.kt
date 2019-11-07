package com.reward.dne.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.AppDetailActivity
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.*
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.android.synthetic.main.fragment_help.*
import kotlinx.android.synthetic.main.fragment_stories.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class StoriesFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var app_id:Int = 0
    var appName = ""
    var storyDetailResponse:StoryDetailResponse?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RewardApplication.instance.getComponent().inject(this)

        val args = arguments
        app_id = args?.getInt("app_id")!!
        appName = args.getString("app_name")!!
        //(activity as AppDetailActivity).headerAppDetail.text =  appName

        getstoryDetail()
    }

    private fun getstoryDetail() {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val storyparam = InputParams()
            storyparam.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            storyparam.story_id = 10

            rewardAPI.getstoryDetail(storyparam).enqueue(object : Callback<StoryDetailResponse> {
                override fun onResponse(call: Call<StoryDetailResponse>, response: Response<StoryDetailResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        storyDetailResponse = response.body()
                        if (storyDetailResponse!!.status == UtilsDefault.STATUS_SUCCESS) {

                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            textTitle.text = storyDetailResponse?.data?.story_title
                            storyDes.text = storyDetailResponse?.data?.story_description

                            Toast.makeText(activity, storyDetailResponse!!.message, Toast.LENGTH_SHORT).show()
                        } else {
                            hideProgress()
                            Toast.makeText(context, storyDetailResponse!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoryDetailResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }


}
