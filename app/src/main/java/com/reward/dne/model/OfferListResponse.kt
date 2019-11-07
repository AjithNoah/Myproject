package com.reward.dne.model


// result generated from /json


data class OfferListResponse(val status: Int?, val message: String?, val Totalpoint: Float?,
                val Totalamount: Double?, val data: List<Offerlist>?)

data class Offerlist(val id: Int?, val country: Int?, val order: String?, val device_type: Int?,
                         val url: String?, val name: String?, val company_name: String?, val points: Int?, 
                         val description: String?, val note: String?, val image: String?, val category: Int?,
                         val offer_id: Int?, val offer_type: Int?, val original_track_link: String?, 
                         val download_start_date: String?, val download_end_date: String?, val start_count: Int?, 
                         val download_limit: Int?, val release_date: String?, val type: Int?,
                         val created_at: String?, val updated_at: String?, val status: Int?, val created_by: Int?,
                         val ch_id: Int?, val ch_user_id: Int?, val ch_activity_id: Int?, val ch_referer_id: Int?,
                         val ch_points: Int?, val ch_status: Int?,val CompletedStatus:String?)

