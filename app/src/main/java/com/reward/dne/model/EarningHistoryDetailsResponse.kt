package com.reward.dne.model


data class EarningHistoryDetailsResponse (val status: Int?, val message: String?, val Points: Double?,
                                          val Amount: Double?, val data: List<EarningHistory>?)

data class EarningHistory(val ch_id: Int?, val ch_user_id: Int?, val ch_activity_id: Int?,
                          val ch_referer_id: Int?, val ch_points: Int?, val ch_status: Int?,
                          val created_at: String?, val updated_at: String?, val name: String?, 
                          val app_icon: String?, val type: String?,val amount:Int?, val created_date: String?,
                          val updated_date: String?)
