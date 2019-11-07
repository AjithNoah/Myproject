package com.reward.dne.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_app_detail.*
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.reward.dne.R
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.InputParams
import com.reward.dne.model.OfferDetailResponse
import com.reward.dne.model.PostBackResponse
import com.reward.dne.model.ShareResponse
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


class AppDetailActivity : BaseActivity() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var appName = ""
    var type: Int = 0
    var offer_id: Int = 0
    var app_id:Int = 0
    var offerDetailResponse: OfferDetailResponse? = null
    var postBackResponse:PostBackResponse ? = null
    var shareResponse: ShareResponse? = null
    var trackingLink = ""
    var click = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        RewardApplication.instance.getComponent().inject(this)
        val intent = intent

        if (intent != null) {
            type = intent.getIntExtra("type", 0)
            appName = intent.getStringExtra("app_name")
            app_id = intent.getIntExtra("app_id",0)
            if (type == 1){
                textInstall.text = "Install"
                textclickHere.text = "Click here to download the app from Play Store"
            }
            else if (type == 2){
                textInstall.text = "View"
                textclickHere.text = "Click here to view the story in given link"
            }
            else if (type ==3){
                textInstall.text = "View"
                textclickHere.text = "Click here to complete the form in given link"
            }
            headerAppDetail.text = appName
            getAppDetails(app_id)
        }

        relInstall.setOnClickListener {
            shareApi(app_id)
        }

        imgBackarrow.setOnClickListener {
            onBackPressed()
        }

    }

    // implement after completion of trackier

    override fun onResume() {
        super.onResume()

       /* if(click){
            postbackCall()
        }*/

    }

    private fun postbackCall(){

        if (UtilsDefault.isOnline()) {
            showProgress()
            val offerParams = InputParams()
            offerParams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            offerParams.offer_id = offer_id
            rewardAPI.callpostback(offerParams).enqueue(object : Callback<PostBackResponse> {
                override fun onResponse(call: Call<PostBackResponse>, response: Response<PostBackResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        postBackResponse = response.body()
                        if (postBackResponse!!.status == UtilsDefault.STATUS_SUCCESS) {
                            click = false
                            val header = response.headers().get("Authorization")
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            try {
                                Toast.makeText(this@AppDetailActivity, postBackResponse!!.message, Toast.LENGTH_SHORT).show()
                                finish()

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        } else {
                            Toast.makeText(this@AppDetailActivity, postBackResponse!!.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AppDetailActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PostBackResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(this@AppDetailActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(this@AppDetailActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareApi(app_id: Int) {

        if (UtilsDefault.isOnline()) {
            showProgress()
            val offerParams = InputParams()
            val userid = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            offerParams.user_id = userid
            offerParams.app_id = app_id
            rewardAPI.share(offerParams).enqueue(object : Callback<ShareResponse> {
                override fun onResponse(call: Call<ShareResponse>, response: Response<ShareResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        shareResponse = response.body()
                        if (shareResponse!!.status == UtilsDefault.STATUS_SUCCESS) {
                            val header = response.headers().get("Authorization")
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }
                            click = true
                            try {
                                if (trackingLink != "0"){
                                    val redirectlink = "$trackingLink?p2=$userid&p3=$offer_id"
                                    Log.d("redirectlink", redirectlink)
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(redirectlink)))
                                }
                                else {
                                    Toast.makeText(this@AppDetailActivity, "Tracking link not available", Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        } else {
                            try {
                                if (!trackingLink.equals("0")){
                                    val redirectlink = "$trackingLink?p2=$userid&p3=$offer_id"
                                    Log.d("redirectlink", redirectlink)
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(redirectlink)))
                                }
                                else {
                                    Toast.makeText(this@AppDetailActivity, "Tracking link not available", Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        Toast.makeText(this@AppDetailActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ShareResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(this@AppDetailActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(this@AppDetailActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }


    fun getAppDetails(appId: Int) {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val offerParams = InputParams()
            offerParams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            offerParams.offer_id = appId
            rewardAPI.getAppDetails(offerParams).enqueue(object : Callback<OfferDetailResponse> {
                override fun onResponse(call: Call<OfferDetailResponse>, response: Response<OfferDetailResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        offerDetailResponse = response.body()
                        if (offerDetailResponse!!.status == UtilsDefault.STATUS_SUCCESS) {
                            val header = response.headers().get("Authorization")
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            try {
                                if (offerDetailResponse!!.data != null) {
                                    textAppName.text = offerDetailResponse!!.data!!.name?.replace("null", "")!!.trim()
                                    val url = getString(R.string.image_url) + offerDetailResponse!!.data!!.image
                                    Glide.with(this@AppDetailActivity).load(url)
                                            .apply(RequestOptions().placeholder(R.drawable.android_default).error(R.drawable.android_default))
                                            .into(imgApplogo)
                                    textAppDesc.text = offerDetailResponse!!.data!!.description
                                    if (offerDetailResponse!!.data?.type == 1){
                                        textAppPoints.text = "Download & Earn ${offerDetailResponse!!.data!!.points} points"
                                    }
                                    else {
                                        textAppPoints.text = "View & Earn ${offerDetailResponse!!.data!!.points} points"
                                    }

                                    trackingLink = offerDetailResponse?.data?.original_track_link!!
                                    offer_id = offerDetailResponse?.data?.offer_id!!
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        } else {
                            Toast.makeText(this@AppDetailActivity, offerDetailResponse!!.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AppDetailActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OfferDetailResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(this@AppDetailActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(this@AppDetailActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }


}
