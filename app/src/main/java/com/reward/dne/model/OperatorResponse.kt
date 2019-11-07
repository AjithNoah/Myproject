package com.reward.dne.model

data class OperatorResponse(val status: Int?, val message: String?, val data: List<OperatorList>?)

data class OperatorList(val id: Int?, val name: String?, val type: String?,
                        val image: String?, val status: Int?, val created_at: String?, val updated_at: String?)