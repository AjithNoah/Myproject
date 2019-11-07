package com.reward.dne.fragment


import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.adapter.AdapterCompleted
import com.reward.dne.adapter.AdapterEarningHistory
import com.reward.dne.adapter.AdapterRedeemHistory
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.*
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.fragment_completed.view.*
import kotlinx.android.synthetic.main.fragment_earning_history.*
import kotlinx.android.synthetic.main.fragment_redeem_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class RedeemHistoryFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var redeemHistoryResponse: RedeemHistoryResponse? = null
    var adapter: AdapterRedeemHistory? = null
    var list = ArrayList<RedeemHistoryList>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redeem_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
        if (userVisibleHint) {
            redeemHistory()
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
           redeemHistory()
        }
    }

    private fun redeemHistory() {
        if (UtilsDefault.isOnline()) {
            showProgress()
            list.clear()
            val checkuserparams = InputParams()
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.redeemhistory(checkuserparams).enqueue(object : Callback<RedeemHistoryResponse> {

                override fun onResponse(call: Call<RedeemHistoryResponse>, response: Response<RedeemHistoryResponse>) {
                    hideProgress()
                    redeemHistoryResponse = response.body()
                    Log.i(ContentValues.TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {

                        val header = response.headers().get(getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }

                        if (redeemHistoryResponse?.status == UtilsDefault.STATUS_SUCCESS) {

                            list.addAll(redeemHistoryResponse?.data!!)

                            if (list.size > 0){
                                textNoHistory.visibility = View.GONE
                                recycleRedeemHistory?.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
                                adapter = AdapterRedeemHistory(activity!!,list)
                                recycleRedeemHistory?.adapter = adapter
                            }
                            else {
                                textNoHistory.visibility = View.VISIBLE
                            }

                        } else {
                            textNoHistory.visibility = View.VISIBLE
                        }

                    } else {
                        textNoHistory.visibility = View.VISIBLE
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<RedeemHistoryResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }


            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }
    }

}
