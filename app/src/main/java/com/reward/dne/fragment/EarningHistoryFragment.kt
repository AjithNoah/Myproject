package com.reward.dne.fragment


import android.content.ContentValues.TAG
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import com.reward.dne.adapter.AdapterEarningHistory
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.*
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_earning_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class EarningHistoryFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var earningResponse: EarningHistoryDetailsResponse? = null
    var adapter : AdapterEarningHistory? = null
    var list = ArrayList<EarningHistory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_earning_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
        mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.earning_history)
        earningHistory()
    }

    private fun earningHistory() {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.earninghistory(checkuserparams).enqueue(object : Callback<EarningHistoryDetailsResponse> {

                override fun onResponse(call: Call<EarningHistoryDetailsResponse>, response: Response<EarningHistoryDetailsResponse>) {
                    hideProgress()
                    earningResponse = response.body()
                    Log.i(TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        val header = response.headers().get(getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }

                        if (earningResponse?.status == UtilsDefault.STATUS_SUCCESS) {
                            list.addAll(earningResponse!!.data!!)
                            if(list.size > 0){
                                textNoEarnedApps.visibility = View.GONE
                                recycleEarningHistory.visibility = View.VISIBLE
                                recycleEarningHistory.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                                adapter = AdapterEarningHistory(activity!!,list,earningResponse?.Amount!!.toString())
                                recycleEarningHistory.adapter = adapter
                            }else{
                                recycleEarningHistory.visibility = View.GONE
                                textNoEarnedApps.visibility = View.VISIBLE
                                textNoEarnedApps.text = earningResponse!!.message.toString()
                            }

                        } else {
                            recycleEarningHistory.visibility = View.GONE
                            textNoEarnedApps.visibility = View.VISIBLE
                            textNoEarnedApps.text = earningResponse!!.message.toString()
                        }
                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<EarningHistoryDetailsResponse>, t: Throwable) {
                    hideProgress()
                    Log.d("Failure",t.message)
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }

            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }
    }


}
