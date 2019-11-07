package com.reward.dne.activity


import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.reward.dne.BuildConfig
import com.reward.dne.R
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.PaytmOrderResponse
import com.reward.dne.model.InputParams
import com.reward.dne.retrofit.RewardAPI
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.android.synthetic.main.activity_redeem.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import com.reward.dne.model.PaytmResponse
import com.reward.dne.model.PaytmVerifyOrder
import com.reward.dne.utils.*
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.Body

import retrofit2.http.POST
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class RedeemActivity : BaseActivity() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var paytmOrderResponse: PaytmOrderResponse? = null
    var paytmVerifyOrder: PaytmVerifyOrder? = null
    var paytmResponse: PaytmResponse? = null
    var TAG: String = "RedeemActivity"
    var count = 0
    var dialogp: Dialog? = null
    var amount = ""
    var paytmmobile = ""

    var mobile = ""
    var money = ""
    var email = ""
    var user_id = 0
    var availableAmount =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem)
        RewardApplication.instance.getComponent().inject(this)
        dialogp = Dialog(this, android.R.style.Theme_Light)
        dialogp?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogp?.setCancelable(false)
        dialogp?.setContentView(R.layout.layout_dialog)
        imgBackarrowRedeem.setOnClickListener {
            onBackPressed()
        }
        val intent = intent
        if (intent!=null){
            availableAmount =  intent.getIntExtra("availableAmount",0)
        }
        user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
        relativeContinue.setOnClickListener {
            var parsemoney = 0
            email = editEmailRedeem.text.toString().trim()
            mobile = editMobileRedeem.text.toString().trim()
            money = editAmountRedeem.text.toString().trim()
            if (money.isNotEmpty()){
                try {
                    parsemoney = money.toInt()
                }
                catch (e:Exception){
                    e.printStackTrace()
                }

            }


            if (mobile.length != 10) {
                editMobileRedeem.error = getString(R.string.enter_valid_mobile)
            }
            else if (parsemoney == 0){
                editAmountRedeem.error = "Enter valid amount"
            } else if (availableAmount  < parsemoney){
                editAmountRedeem.error = "Balance is low than amount you entered"
            }
            else {
                editMobileRedeem.setText("")
                editAmountRedeem.setText("")
                requestOrder(mobile,money)
            }


        }
    }

    override fun onResume() {
        super.onResume()
       // count = 0
    }

    private fun requestOrder(mobile: String,money:String) {
        paytmmobile = mobile
        amount = money
        if (UtilsDefault.isOnline()) {
            showProgress()
            val offerParams = InputParams()
            offerParams.amount = money
            offerParams.beneficiaryPhoneNo = mobile
            offerParams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.getpaytmorder(offerParams).enqueue(object : Callback<PaytmOrderResponse> {
                override fun onResponse(call: Call<PaytmOrderResponse>, response: Response<PaytmOrderResponse>) {
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        hideProgress()
                        paytmOrderResponse = response.body()

                        if (paytmOrderResponse?.status == UtilsDefault.STATUS_SUCCESS) {
                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            if (paytmOrderResponse?.data?.orderId != null) {
                                transaction("Success", paytmOrderResponse?.data?.orderId!!,1)
                                // Toast.makeText(this@RedeemActivity, paytmOrderResponse?.message + " Please wait..", Toast.LENGTH_SHORT).show()
                              //  verifyOrder(paytmOrderResponse?.data?.orderId!!)
                               // transaction("Your order accepted please wait till we verify your order and we redirect to the bank server.",paytmOrderResponse?.data?.orderId!!,0)
                            }
                        } else {
                            Toast.makeText(this@RedeemActivity, paytmOrderResponse?.message, Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@RedeemActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaytmOrderResponse>, t: Throwable) {
                    hideProgress()
                    Log.d(TAG, t.message)
                    Toast.makeText(this@RedeemActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(this@RedeemActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun transaction(status: String,orderID: String,type:Int) {


        val textReference = dialogp?.findViewById<CustomTextViewNormal>(R.id.textReference)
        val textTxnId = dialogp?.findViewById<CustomTextViewNormal>(R.id.textTxnId)
        val textTime = dialogp?.findViewById<CustomTextViewNormal>(R.id.textTime)
        val textdonot = dialogp?.findViewById<CustomTextViewNormal>(R.id.textdonot)
        val textSent = dialogp?.findViewById<CustomTextViewNormal>(R.id.textSent)
        val textAccepted = dialogp?.findViewById<CustomTextViewSemiBold>(R.id.textAccepted)
        val textAmount = dialogp?.findViewById<CustomTextViewBold>(R.id.textAmount)
        val textSentNumber = dialogp?.findViewById<CustomTextViewBold>(R.id.textSentNumber)
        val btnClose = dialogp?.findViewById<Button>(R.id.btnClose)
        val progress = dialogp?.findViewById<ProgressBar>(R.id.progress)
        val imgTick = dialogp?.findViewById<ImageView>(R.id.imgTick)
        val btnRetry = dialogp?.findViewById<Button>(R.id.btnRetry)

        val date = Date()
        val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
        val answer: String = formatter.format(date)
        textTime?.text = answer

        textAmount?.text = "₹"+amount
        textSentNumber?.text = paytmmobile

        textTxnId?.text = "Order Id : "+orderID
        btnRetry?.visibility = View.GONE

        if (type == 1) {
            progress?.visibility = View.GONE
            btnClose?.visibility = View.VISIBLE
            textAccepted?.text = "Order Success"
            textdonot?.visibility = View.GONE
            btnRetry?.visibility = View.GONE
            textReference?.text = "Successfully transfered to payTM Wallet."
            textSent?.text = "Payment processed"
        }/*else if (type == 2) {
            progress?.visibility = View.GONE
            btnClose?.visibility = View.VISIBLE
            btnRetry?.visibility = View.VISIBLE
            textAccepted?.text = "Request Failed"
            textdonot?.visibility = View.GONE
            imgTick?.setImageResource(R.mipmap.redcross)
            textSent?.text = "Payment process failed"
        }
        else if (type == 0){
            progress?.visibility = View.VISIBLE
            btnClose?.visibility = View.GONE
            btnRetry?.visibility = View.GONE
            textAccepted?.text = "Request Accepted"
            textdonot?.visibility = View.VISIBLE
            imgTick?.setImageResource(R.mipmap.greentick)
            count = 0
        }*/
        btnClose?.setOnClickListener {
            dialogp?.dismiss()
            finish()
        }
        btnRetry?.setOnClickListener {
            //verifyOrder(orderID)
            progress?.visibility = View.VISIBLE
            btnClose?.visibility = View.GONE
            btnRetry.visibility = View.GONE
            textAccepted?.text = "Request Accepted"
            textdonot?.visibility = View.VISIBLE
            imgTick?.setImageResource(R.mipmap.greentick)
            textSent?.text = "Your order accepted please wait till we verify your order and we redirect to the bank server."
            count = 0
        }

        dialogp?.show()
    }


   /* private fun verifyOrder(orderID: String) {
        if (UtilsDefault.isOnline()) {

            val offerParams = InputParams()
            offerParams.orderId = orderID
            offerParams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.verifypaytm(offerParams).enqueue(object : Callback<PaytmVerifyOrder> {
                override fun onResponse(call: Call<PaytmVerifyOrder>, response: Response<PaytmVerifyOrder>) {


                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        hideProgress()
                        paytmVerifyOrder = response.body()

                        if (paytmVerifyOrder?.status == UtilsDefault.STATUS_SUCCESS) {
                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }
                            if (paytmVerifyOrder?.statusCode == UtilsDefault.PAYTM_SUCCESS){
                                hideProgress()
                                dialogp?.dismiss()
                                Thread.currentThread().interrupt()
                                transaction(paytmVerifyOrder?.message!!,orderID,1)
                                Toast.makeText(this@RedeemActivity, paytmVerifyOrder?.message, Toast.LENGTH_SHORT).show()
                            }
                            else {
                                transaction(paytmVerifyOrder?.message!!,orderID,2)
                            }

                        } else if (paytmVerifyOrder?.status == UtilsDefault.PAYTM_FAILURE) {
                            count++
                            if (count == 6) {
                              //  dialogp?.dismiss()
                                hideProgress()
                                // finish()
                                Thread.currentThread().interrupt()
                                transaction(paytmVerifyOrder?.message!!,orderID,2)
                                Toast.makeText(this@RedeemActivity, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show()
                            } else {
                                Handler().postDelayed({

                                    verifyOrder(orderID)

                                }, 10000)
                            }

                        }

                    } else {
                        dialogp?.dismiss()
                        hideProgress()
                        Toast.makeText(this@RedeemActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaytmVerifyOrder>, t: Throwable) {
                    hideProgress()
                    dialogp?.dismiss()
                    Log.d(TAG, t.message)
                    Toast.makeText(this@RedeemActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(this@RedeemActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }

    }*/


    private fun payTmRedeem(checsum: String) {


        val baseurl = "https://staging-dashboard.paytm.com/bpay/api/v1/disburse/order/"
        var produrl = "https://dashboard.paytm.com/bpay/api/v1/disburse/order"
        val mid = "Enroot13635591906958"

        val requestBody = getStagingRequst()

        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.interceptors().add(logging)
        }

        okHttpBuilder.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val original = chain.request()
                var request: Request? = null
                request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-mid", mid)
                        .addHeader("x-checksum", checsum)
                        .method(original.method(), original.body())
                        .build()
                return chain.proceed(request!!)

            }

        })
        val retrofit = Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpBuilder.build())
                .build()


        val api = retrofit.create(PaytmInterface::class.java)


        val call = api.paytm(requestBody)
        call.enqueue(object : Callback<PaytmResponse> {
            override fun onFailure(call: Call<PaytmResponse>, t: Throwable) {
                Log.d(TAG, t.message)
            }

            override fun onResponse(call: Call<PaytmResponse>, response: Response<PaytmResponse>) {
                Log.d(TAG, response.body().toString())

                if (response.body() != null && response.isSuccessful && response.code() == 200) {
                    paytmResponse = response.body()

                    Toast.makeText(this@RedeemActivity, paytmResponse?.statusMessage, Toast.LENGTH_SHORT).show()
                } else if (response.code() == 401) {
                    try {
                        var jObjError = JSONObject(response.errorBody()?.string());
                        Toast.makeText(this@RedeemActivity, jObjError.getString("statusMessage"), Toast.LENGTH_SHORT).show();
                    } catch (e: Exception) {
                        Toast.makeText(this@RedeemActivity, e.message, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 403) {
                    Toast.makeText(this@RedeemActivity, "forbidden", Toast.LENGTH_SHORT).show();
                }
            }

        })

    }

    interface PaytmInterface {

        @POST("bank")
        fun paytm(@Body body: String): Call<PaytmResponse>
    }

    private fun getStagingRequst(): String {
        val paytmSalesWalletGuid = "6fdb6980-d13e-11e9-8708-fa163e429e83"
        val requestBody = JsonObject()

        requestBody.add("orderId", JsonPrimitive("5151651"))
        requestBody.add("subwalletGuid", JsonPrimitive(paytmSalesWalletGuid))
        requestBody.add("beneficiaryAccount", JsonPrimitive("919899996782"))
        requestBody.add("beneficiaryIFSC", JsonPrimitive("PYTM0123456"))
        requestBody.add("purpose", JsonPrimitive("REIMBURSEMENT"))
        requestBody.add("date", JsonPrimitive("2019-09-18"))
        requestBody.add("amount", JsonPrimitive("1.00"))

        return Gson().toJson(requestBody).toString()

    }

}
