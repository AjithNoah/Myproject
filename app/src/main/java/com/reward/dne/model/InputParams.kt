package com.reward.dne.model

class InputParams {

    //Login
    var mobile:String? = null
    var packageID:String? = null

    //help submit
    var user_id:Int? = null
    var name:String? = null
    var firstname:String? = null
    var gender:Int? =null
    var invite_code:String? = null
    var subject:String? = null
    var message:String? = null
    var email:String? = null

    //signup
    var fcm_key:String?=null
    var device_id:String?=null

    //Offer page
    var type:Int? = null
    var rechargetype:String? =null

    //Offer Detail
    var app_id:Int? = null
    var offer_id:Int? =null

    //getstories
    var story_id:Int?=null

    //checksum
    var amount:String?=null
    var beneficiaryPhoneNo:String? =null
    var orderId:String? = null

}