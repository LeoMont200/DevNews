package com.avanade.devnews.data.remote.api

import com.avanade.devnews.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/everything")
    suspend fun getEverything(
        @Query("apiKey") apiKey: String,
        @Query("q") query: String,
        @Query("sortBy") sortBy: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponseDto
}
