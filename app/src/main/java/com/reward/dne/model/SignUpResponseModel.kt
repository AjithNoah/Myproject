package com.reward.dne.model

data class SignUpResponseModel(val status: Int?, val message: String?, val data: Data?)

data class Data(val mobile: String?, val type: Int?, val gender: Int?, val email: String?, 
                val firstname: String?, val lastname: String?, val avatar: String?, 
                val app_version: String?, val invite_code: String?, val fcm_key: String?,
                val device_id: String?, val referral_code: String?, val status: Int?,
                val updated_at: String?, val created_at: String?, val id: Int?)