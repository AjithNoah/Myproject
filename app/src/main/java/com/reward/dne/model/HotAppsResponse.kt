package com.reward.dne.model


data class HotAppsResponse(val status: Int?, val message: String?, val data: List<HotAppList>?)

data class HotAppList(val id: Int?, val country: Int?, val order: String?, val device_type: Int?,
                          val url: String?, val name: String?, val company_name: String?, val points: Int?,
                          val description: String?, val note: String?, val image: String?, val category: Int?, 
                          val offer_id: Int?, val offer_type: Int?, val original_track_link: String?, 
                          val download_start_date: String?, val download_end_date: String?, val start_count: Int?,
                          val download_limit: Int?, val release_date: String?, val is_hot_app: Int?,
                          val type: Int?, val created_at: String?, val updated_at: String?, val status: Int?, 
                          val created_by: Int?)