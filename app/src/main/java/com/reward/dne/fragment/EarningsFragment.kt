package com.reward.dne.fragment


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import com.reward.dne.activity.RedeemActivity
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.PaytmOrderResponse
import com.reward.dne.model.EarningResponse
import com.reward.dne.model.InputParams
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_earnings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.JsonPrimitive
import com.google.gson.JsonObject
import io.reactivex.Observable
import java.net.URL
import java.io.*
import javax.net.ssl.HttpsURLConnection


class EarningsFragment : BaseFragment(){

    @Inject
    lateinit var rewardAPI: RewardAPI
    var earningResponse: EarningResponse? = null
    var TAG: String = "EarningsFragment"
    var availableAmount = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_earnings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.earnings)

        relativeRedeem.setOnClickListener {
            if (availableAmount == 0 || availableAmount < 0){
                Toast.makeText(activity, "Your current balance is too low to make a transaction", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(activity, RedeemActivity::class.java)
                intent.putExtra("availableAmount",availableAmount)
                startActivity(intent)
            }

        }

    }

    override fun onResume() {
        super.onResume()
        getEarnings()
    }

    private fun getEarnings() {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.earning(checkuserparams).enqueue(object : Callback<EarningResponse> {

                override fun onResponse(call: Call<EarningResponse>, response: Response<EarningResponse>) {
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

                            if (earningResponse?.data!=null){
                                textmyTotal.text = earningResponse?.data?.total.toString()
                                availableAmount = earningResponse?.data?.available!!
                                textmyAvailable.text = earningResponse?.data?.available.toString()
                                textmyPending.text = earningResponse?.data?.redeemAmount.toString()
                                textmyTotalPoints.text = earningResponse?.data?.totalpoints.toString()

                                UtilsDefault.updateSharedPreferenceInt(Constants.MY_EARNED_AMOUNT,earningResponse?.data?.available!!.toInt() )
                                MainActivity.tvAmount?.text = "₹" + UtilsDefault.getSharedPreferenceInt(Constants.MY_EARNED_AMOUNT)
                                MainActivity.tvMenuAmount?.text = "₹" + UtilsDefault.getSharedPreferenceInt(Constants.MY_EARNED_AMOUNT)

                                if (availableAmount == 0){
                                    relativeRedeem.alpha = 0.5F
                                }
                                else {
                                    relativeRedeem.alpha = 1.0F
                                }
                            }

                        } else {
                            Toast.makeText(activity, earningResponse!!.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<EarningResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }


            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }

    }

}
