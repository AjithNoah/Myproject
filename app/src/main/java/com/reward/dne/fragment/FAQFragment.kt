package com.reward.dne.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import com.reward.dne.adapter.AdapterFAQ
import com.reward.dne.adapter.AdapterHotApps
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.FAQResponse
import com.reward.dne.model.modelFAQ
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_faq.*
import kotlinx.android.synthetic.main.fragment_hot_apps.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class FAQFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var adapter: AdapterFAQ? = null
    var faqList: ArrayList<modelFAQ> = ArrayList()
    var faqResponse:FAQResponse? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
        mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.faq)

        recycleFaq.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        adapter = AdapterFAQ(activity!!, faqList)
        recycleFaq.adapter = adapter
        getFaq()
    }

    fun getFaq() {
        if (UtilsDefault.isOnline()) {
            showProgress()
            rewardAPI.getFaq().enqueue(object : Callback<FAQResponse> {
                override fun onResponse(call: Call<FAQResponse>, response: Response<FAQResponse>) {
                    hideProgress()
                        if (response.body() != null && response.isSuccessful && response.code() == 200) {
                            faqResponse = response.body()

                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            if (faqResponse?.status == UtilsDefault.STATUS_SUCCESS){
                                faqList.clear()

                                if (faqResponse!!.data != null && faqResponse!!.data!!.size > 0) {
                                    for (i in 0 until faqResponse!!.data!!.size) {
                                        faqList.add(modelFAQ(faqResponse!!.data!![i].faq_question!!, faqResponse!!.data!![i].faq_answer!!))
                                    }
                                    if (faqList.size > 0) {
                                        adapter?.notifyDataSetChanged()
                                    }
                                } else {
                                    Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }

                }

                override fun onFailure(call: Call<FAQResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

}
