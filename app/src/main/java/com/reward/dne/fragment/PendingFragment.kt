package com.reward.dne.fragment


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.reward.dne.activity.MainActivity
import com.reward.dne.adapter.AdapterPending
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.InputParams
import com.reward.dne.model.OfferListResponse
import com.reward.dne.model.Offerlist
import com.reward.dne.model.modelOfferList
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.OnItemClickListener
import com.reward.dne.utils.UtilsDefault
import com.reward.dne.utils.addOnItemClickListener
import kotlinx.android.synthetic.main.fragment_offer.view.*
import kotlinx.android.synthetic.main.fragment_pending.*
import kotlinx.android.synthetic.main.fragment_pending.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


class PendingFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var adapter: AdapterPending? = null
    val list: ArrayList<Offerlist> = ArrayList()
    var modelOfferList: OfferListResponse? = null
    lateinit var recyclePending:RecyclerView
    var TAG: String = "PendingFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pending, container, false)
        view?.tag = 1
        recyclePending = view.findViewById(R.id.recyclePending)
        RewardApplication.instance.getComponent().inject(this)
        if (userVisibleHint) {
            getAppList()
        }

        recyclePending.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(pos: Int, view: View) {

                val intent = Intent(activity, AppDetailActivity::class.java)
                intent.putExtra("app_id", list[pos].id)
                intent.putExtra("type", list[pos].type)
                intent.putExtra("app_name", list[pos].name)
                intent.putExtra("offer_id", list[pos].offer_id)
                startActivity(intent)

            }

        })
        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            getAppList()
        }
    }


    fun getAppList() {
        if (UtilsDefault.isOnline()) {
            list.clear()
            showProgress()
            val offerParams = InputParams()
            offerParams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            offerParams.type = 1
            rewardAPI.getAppList(offerParams).enqueue(object : Callback<OfferListResponse> {
                override fun onResponse(call: Call<OfferListResponse>, response: Response<OfferListResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        modelOfferList = response.body()

                        try {
                            UtilsDefault.updateSharedPreferenceInt(Constants.MY_EARNED_AMOUNT, modelOfferList!!.Totalamount!!.toInt())
                            MainActivity.tvAmount?.text = "₹" + UtilsDefault.getSharedPreferenceInt(Constants.MY_EARNED_AMOUNT)
                            MainActivity.tvMenuAmount?.text = "₹" + UtilsDefault.getSharedPreferenceInt(Constants.MY_EARNED_AMOUNT)
                        }
                        catch (e:Exception){
                            e.printStackTrace()
                        }


                        if (modelOfferList!!.status == UtilsDefault.STATUS_SUCCESS) {
                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            if (modelOfferList!!.data != null && modelOfferList!!.data!!.size > 0) {
                                list.addAll(modelOfferList!!.data!!)
                                if (list.size > 0) {
                                    textNoApps.visibility = View.GONE
                                    recyclePending.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                                    adapter = AdapterPending(activity!!, list)
                                    recyclePending.adapter = adapter

                                } else {
                                    textNoApps.visibility = View.VISIBLE
                                }
                            } else {
                                textNoApps.visibility = View.VISIBLE
                            }

                        }
                        else if (modelOfferList!!.status == UtilsDefault.TOKEN_NOT_PROVIDED) {
                            // getAppList()
                            logout(activity!!, MainActivity())
                        }
                        else if (modelOfferList!!.status == UtilsDefault.STATUS_TOKENEXPIRE) {
                            //getAppList()
                            logout(activity!!,MainActivity())
                        }
                        else {
                            textNoApps.visibility = View.VISIBLE
                        }
                    }
                    else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OfferListResponse>, t: Throwable) {
                    hideProgress()
                    Log.d(TAG, t.message)
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

}
