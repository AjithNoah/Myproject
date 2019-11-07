package com.reward.dne.model

data class RedeemHistoryResponse(val status: Int?, val message: String?, val data: List<RedeemHistoryList>?)

data class RedeemHistoryList(val rdm_id: Int?, val rdm_redeemtype_id: Int?, val rdm_txnid: String?, 
                         val rdm_orderid: String?, val rdm_user_id: Int?, val rdm_txndate: String?,
                         val rdm_refundamt: String?, val rdm_paymentmode: String?, val rdm_mid: String?,
                         val rdm_bankname: String?, val rdm_respcode: String?, val rdm_gatewayname: String?,
                         val rdm_txntype: String?, val rdm_phone: String?, val rdm_points: Int?, 
                         val rdm_operator: String?, val rdm_operator_type: String?, val rdm_txnamount: String?,
                         val taxAmount: String?, val commissionAmount: String?, val rdm_description: String?,
                         val rdm_status: Int?, val rdm_status_message: String?, val created_at: String?,
                         val updated_at: String?, val images: String?)