package com.reward.dne.model

data class EarningResponse(val status: Int?, val message: String?, val data: EarningData?)

data class EarningData(val total: Int?, val redeemAmount: Int?, val available: Int?,val totalpoints:String?)