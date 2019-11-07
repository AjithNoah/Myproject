package com.reward.dne.retrofit

import android.arch.lifecycle.Observer
import com.reward.dne.model.CheckUserResponseModel
import com.reward.dne.model.InputParams
import com.reward.dne.model.LoginResponse
import com.reward.dne.model.SignUpResponseModel
import com.reward.dne.model.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RewardAPI {

   /* @FormUrlEncoded
    @POST("signup")
    fun signup(@Field("mobile") mobile: String, @Field("fcm_key") fcm_key: String,
               @Field("device_id") device_id: String,
               @Field("invite_code") invite_code: String): Call<SignUpResponseModel>*/

    @POST("signup")
    fun signup(@Body loginParams: InputParams): Call<SignUpResponseModel>

    @POST("login")
    fun login(@Body loginParams: InputParams): Call<LoginResponse>

    @POST("CheckUserExist")
    fun checkuser(@Body checkuser: InputParams): Call<CheckUserResponseModel>

    @POST("HelpSubmit")
    fun helpSubmit(@Body loginParams: InputParams): Call<OutputResponse>

    @POST("Earnings")
    fun earning(@Body loginParams: InputParams): Call<EarningResponse>

    @POST("EarningHistory")
    fun earninghistory(@Body loginParams: InputParams): Call<EarningHistoryDetailsResponse>

    @POST("RedeemHistory")
    fun redeemhistory(@Body loginParams: InputParams): Call<RedeemHistoryResponse>

    @POST("Redeem")
    fun rechargetype(@Body loginParams: InputParams): Call<OperatorResponse>

    @POST("UserUpdateProfile")
    fun updateprofile(@Body loginParams: InputParams): Call<UpdateProfileResponse>

    @POST("GetUserData")
    fun getuserdata(@Body loginParams: InputParams): Call<GetUserDataResponse>

    @POST("getHotApps")
    fun hotappslist(@Body loginParams: InputParams): Call<HotAppsResponse>

    @POST("getHotApps")
    fun hotappslistrx(@Body loginParams: InputParams): Observable<Response<HotAppsResponse>>

    @POST("getStoriesDetails")
    fun getstoryDetail(@Body loginParams: InputParams): Call<StoryDetailResponse>

    @POST("GetFaq")
    fun getFaq(): Call<FAQResponse>

    @POST("getAppList")
    fun getAppList(@Body loginParams: InputParams): Call<OfferListResponse>

    @POST("paytmchecksumverification")
    fun getpaytmorder(@Body loginParams: InputParams): Call<PaytmOrderResponse>

    @POST("PaytmOrderDetails")
    fun verifypaytm(@Body loginParams: InputParams): Call<PaytmVerifyOrder>

    @POST("GetOTP")
    fun sendotp(@Body loginParams: InputParams): Call<OtpResponse>

    @POST("getAppDetails")
    fun getAppDetails(@Body loginParams: InputParams): Call<OfferDetailResponse>

    @POST("downloadresponse")
    fun callpostback(@Body loginParams: InputParams): Call<PostBackResponse>

    @POST("CheckUserDeviceId")
    fun checkdevice(@Body loginParams: InputParams): Call<DeviceResponse>

    @POST("Share")
    fun share(@Body loginParams: InputParams): Call<ShareResponse>


}
