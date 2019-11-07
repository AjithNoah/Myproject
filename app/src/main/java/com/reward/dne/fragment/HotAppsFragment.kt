package com.reward.dne.fragment


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
import com.reward.dne.R.id.recycleHotApps
import com.reward.dne.R.id.textNoApps
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import com.reward.dne.adapter.AdapterHotApps
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.*
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_hot_apps.*
import kotlinx.android.synthetic.main.fragment_offer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class HotAppsFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var hotAppsResponse: HotAppsResponse? = null
    var TAG: String = "HotAppsFragment"
    var adapter: AdapterHotApps? = null
    var list: ArrayList<HotAppList> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hot_apps, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.hotapps)
         hotAppsApi()
       // hotappRxjava()

    }

    private fun hotappRxjava() {

        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.type = UtilsDefault.getSharedPreferenceInt(Constants.USER_TYPE)
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.hotappslistrx(checkuserparams)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Response<HotAppsResponse>> {
                        override fun onNext(response: Response<HotAppsResponse>) {
                            hideProgress()
                            hotAppsResponse = response.body()
                            Log.i(TAG, response.body().toString())
                            if (response.body() != null && response.isSuccessful && response.code() == 200) {

                                val header = response.headers().get(getString(R.string.token))
                                if (header != null) {
                                    UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                                } else {
                                    UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                                }

                                if (hotAppsResponse?.status == UtilsDefault.STATUS_SUCCESS) {

                                    list.addAll(hotAppsResponse?.data!!)
                                    if (list.size > 0) {
                                        recycleHotApps.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                                        adapter = AdapterHotApps(activity!!, list)
                                        recycleHotApps.adapter = adapter
                                    } else {
                                        Toast.makeText(activity, getString(R.string.no_apps_atthe_moment), Toast.LENGTH_SHORT).show()
                                    }


                                } else {

                                    Toast.makeText(activity, hotAppsResponse!!.message.toString(), Toast.LENGTH_SHORT).show()

                                }
                            } else {

                                Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                            }

                        }

                        override fun onComplete() {
                            Log.i(TAG, "completed")
                        }

                        override fun onSubscribe(p0: Disposable) {
                        }


                        override fun onError(res: Throwable) {
                            hideProgress()
                            Log.i(TAG, res.message)
                            Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                        }

                    })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun hotAppsApi() {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.type = UtilsDefault.getSharedPreferenceInt(Constants.USER_TYPE)
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.hotappslist(checkuserparams).enqueue(object : Callback<HotAppsResponse> {

                override fun onResponse(call: Call<HotAppsResponse>, response: Response<HotAppsResponse>) {
                    hideProgress()
                    hotAppsResponse = response.body()
                    Log.i(TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {

                        val header = response.headers().get(getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }

                        if (hotAppsResponse?.status == UtilsDefault.STATUS_SUCCESS) {

                            list.addAll(hotAppsResponse?.data!!)
                            if (list.size > 0) {
                                textNoHotApps.visibility = View.GONE
                                recycleHotApps.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                                adapter = AdapterHotApps(activity!!, list)
                                recycleHotApps.adapter = adapter
                            } else {
                                textNoHotApps.visibility = View.VISIBLE
                                Toast.makeText(activity, getString(R.string.no_apps_atthe_moment), Toast.LENGTH_SHORT).show()
                            }


                        } else {
                            textNoHotApps.visibility = View.VISIBLE
                            Toast.makeText(activity, hotAppsResponse!!.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        textNoHotApps.visibility = View.VISIBLE
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<HotAppsResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }


            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }
    }

}
