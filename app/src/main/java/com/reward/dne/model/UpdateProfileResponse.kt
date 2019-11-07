package com.reward.dne.model

data class UpdateProfileResponse(val status: Int?, val message: String?, val data: UpdateProfile?)

data class UpdateProfile(val id: Int?, val firstname: String?, val lastname: String?, val email: String?, 
                         val mobile: String?, val type: Int?, val avatar: String?, val gender: Int?, 
                         val total_credit: String?, val app_version: String?, val email_verified_at: String?, 
                         val fcm_key: String?, val device_id: String?, val invite_code: String?,
                         val referral_code: String?, val status: Int?, val created_at: String?, 
                         val updated_at: String?, val created_by: String?)