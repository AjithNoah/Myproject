package com.reward.dne.model

class LoginResponse {

    val status: Int? = 0
    val message: String? = null
    val data: Data? = null

    class Data {
        val id: Int? = 0
        val firstname: String? = null
        val lastname: String? = null
        val email: String? = null
        val mobile: String? = null
        val type: Int? = 0
        val avatar: String? = null
        val gender: Int? = 0
        val total_credit: String? = null
        val app_version: String? = null
        val email_verified_at: String? = null
        val fcm_key: String? = null
        val device_id: String? = null
        val invite_code: String? = null
        val referral_code: String? = null
        val status: Int? = 0
        val created_at: String? = null
        val updated_at: String? = null
        val created_by: String? = null
    }
}
