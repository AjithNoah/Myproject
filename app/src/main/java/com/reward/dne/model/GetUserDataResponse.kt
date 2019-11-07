package com.reward.dne.model

data class GetUserDataResponse(val status: Int?, val message: String?, val data: GetUserdata?)

data class GetUserdata(val id: Int?, val firstname: String?, val lastname: String?, val email: String?,
                val mobile: String?, val type: Int?, val avatar: String?, val gender: Int?, val invite_code: String?,
                val referral_code: String?, val status: Int?, val created_at: String?,
                val updated_at: String?, val created_by: String?)