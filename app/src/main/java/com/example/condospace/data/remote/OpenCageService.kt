package com.example.condospace.data.remote

import com.example.condospace.data.model.OpenCageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenCageService {
    @GET("geocode/v1/json")
    suspend fun getGeocoding(
        @Query("q") query: String,
        @Query("key") apiKey: String = "438716b1abd0467a931bd139da0cbfb8"
    ): OpenCageResponse
}
