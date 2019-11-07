package com.reward.dne.model

class FAQResponse {

    val status: Int? = 0
    val message: String? = null
    val data: ArrayList<Data>? = null

    class Data {
        val faq_id: Int? = 0
        val faq_question: String? = null
        val faq_answer: String? = null
        val faq_status: Int? = 0
        val created_at: String? = null
        val updated_at: String? = null
    }
}
