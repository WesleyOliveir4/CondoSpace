package com.example.condospace.data.remote

import com.example.condospace.data.model.ViaCepResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {
    @GET("ws/{cep}/json/")
    suspend fun getAddressByCep(@Path("cep") cep: String): ViaCepResponse
}
