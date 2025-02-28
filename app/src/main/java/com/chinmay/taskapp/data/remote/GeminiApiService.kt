package com.chinmay.taskapp.data.remote


import com.chinmay.taskapp.domain.model.GeminiRequest
import com.chinmay.taskapp.domain.model.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface GeminiApiService {

    @Headers("Content-Type: application/json")
    @POST
    suspend fun generateTaskDetails(
        @Url fullUrl: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}
