package com.reward.dne.model


data class PaytmOrderResponse(val status: Int?, val message: String?, val data: PaytmData?)

data class PaytmData(val status: String?, val statusCode: String?, val statusMessage: String?, val orderId: String?)