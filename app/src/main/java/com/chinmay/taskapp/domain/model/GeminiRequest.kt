package com.chinmay.taskapp.domain.model


data class GeminiRequest(
    val contents: List<ContentRequest>
)

data class ContentRequest(
    val parts: List<PartRequest>
)

data class PartRequest(
    val text: String
)
