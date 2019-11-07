package com.reward.dne.model

data class StoryDetailResponse(val status: Int?, val message: String?, val data: StoryDetail?)

data class StoryDetail(val story_id: Int?, val story_title: String?, val story_type: Int?, 
                       val story_description: String?, val story_image: String?, val story_points: Int?,
                       val created_by: Int?, val story_status: Int?, val story_start_date: String?,
                       val story_end_date: String?, val created_at: Int?, val updated_at: Int?,
                       val ch_id: Int?, val ch_user_id: Int?, val ch_activity_id: Int?, 
                       val ch_referer_id: Int?, val ch_points: Int?, val ch_status: Int?)

