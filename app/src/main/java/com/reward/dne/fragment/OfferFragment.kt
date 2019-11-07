package com.reward.dne.fragment


import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.AppDetailActivity
import com.reward.dne.activity.LoginActivity
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.tvAmount
import com.reward.dne.adapter.AdapterOffers
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.*
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.*
import kotlinx.android.synthetic.main.fragment_hot_apps.*
import kotlinx.android.synthetic.main.fragment_offer.*
import kotlinx.android.synthetic.main.fragment_offer.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


class OfferFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    lateinit var textNoApps: CustomTextViewSemiBold

    var adapter: AdapterOffers? = null
    val list: ArrayList<ModelAppCheckOffer> = ArrayList()
    var modelOfferList: OfferListResponse? = null
    var TAG: String = "EarningsFragment"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_offer, container, false)
        RewardApplication.instance.getComponent().inject(this)
        textNoApps = view.findViewById(R.id.textNoApps)
        if (userVisibleHint) {
            getAppList()
        }


        view.recycleOffers.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(pos: Int, view: View) {

                val intent = Intent(activity, AppDetailActivity::class.java)
                intent.putExtra("app_id", list[pos].app_id)
                intent.putExtra("type", list[pos].type)
                intent.putExtra("app_name", list[pos].appName)
                intent.putExtra("offer_id", list[pos].offer_id)
                startActivity(intent)
                /*if (list[pos].type == 1) {

                }*/

            }

        })

        return view
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            Log.d(TAG, "offer")
            getAppList()
        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = activity!!.packageManager
        var app_installed = false
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }

        return app_installed
    }


    fun getAppList() {
        if (UtilsDefault.isOnline()) {
            list.clear()
            showProgress()
            val offerParams = InputParams()
            offerParams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            offerParams.type = 0
            rewardAPI.getAppList(offerParams).enqueue(object : Callback<OfferListResponse> {
                override fun onResponse(call: Call<OfferListResponse>, response: Response<OfferListResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        modelOfferList = response.body()
                        Log.d(TAG, "response")

                        val header = response.headers().get(getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }

                        try {
                            UtilsDefault.updateSharedPreferenceInt(Constants.MY_EARNED_AMOUNT, modelOfferList!!.Totalamount!!.toInt())
                            MainActivity.tvAmount?.text = "₹" + UtilsDefault.getSharedPreferenceInt(Constants.MY_EARNED_AMOUNT)
                            MainActivity.tvMenuAmount?.text = "₹" + UtilsDefault.getSharedPreferenceInt(Constants.MY_EARNED_AMOUNT)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                        if (modelOfferList!!.status == UtilsDefault.STATUS_SUCCESS) {

                            if (modelOfferList!!.data != null && modelOfferList!!.data!!.size > 0) {
                                textNoApps.visibility = View.GONE
                                for (i in 0 until modelOfferList!!.data!!.size) {

                                    try {
                                        val type = modelOfferList?.data!![i].type
                                        //type 1 - app
                                        if (type == 1) {
                                            val link = modelOfferList?.data!![i].url
                                            if (link != "") {
                                                list.add(ModelAppCheckOffer(modelOfferList?.data!![i].name!!, modelOfferList?.data!![i].points!!,
                                                        modelOfferList?.data!![i].image!!, modelOfferList?.data!![i].offer_type!!,
                                                        "", modelOfferList?.data!![i].id, modelOfferList?.data!![i].offer_id,
                                                        modelOfferList?.data!![i].type, modelOfferList?.data!![i].original_track_link))
                                            }
                                        }
                                        //type 2- stories 3- forms 4-contest
                                        else {
                                            list.add(ModelAppCheckOffer(modelOfferList?.data!![i].name!!, modelOfferList?.data!![i].points!!,
                                                    modelOfferList?.data!![i].image!!, modelOfferList?.data!![i].offer_type!!,
                                                    "", modelOfferList?.data!![i].id, modelOfferList?.data!![i].offer_id,
                                                    modelOfferList?.data!![i].type, modelOfferList?.data!![i].original_track_link))
                                        }


                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                                if (list.size > 0){
                                    view?.recycleOffers?.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
                                    adapter = AdapterOffers(activity!!, list)
                                    view?.recycleOffers?.adapter = adapter
                                }
                                else {
                                    textNoApps.visibility = View.VISIBLE
                                }


                            } else {
                                textNoApps.visibility = View.VISIBLE
                            }

                        }else if (modelOfferList!!.status == UtilsDefault.TOKEN_NOT_PROVIDED) {
                            // getAppList()
                            logout(activity!!, MainActivity())
                        }else if (modelOfferList!!.status == UtilsDefault.STATUS_TOKENEXPIRE) {
                            //getAppList()
                            logout(activity!!, MainActivity())
                        }else {
                            textNoApps.visibility = View.VISIBLE
                            Toast.makeText(activity, modelOfferList!!.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OfferListResponse>, t: Throwable) {
                    hideProgress()
                    Log.d("offer", t.message)
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

}
