package com.chinmay.taskapp.domain.model


import com.google.gson.annotations.SerializedName

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<Candidate>?
)

data class Candidate(
    @SerializedName("content") val content: ContentResponse?
)

data class ContentResponse(
    @SerializedName("parts") val parts: List<PartResponse>?
)

data class PartResponse(
    @SerializedName("text") val text: String?
)
