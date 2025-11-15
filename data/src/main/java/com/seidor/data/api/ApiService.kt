package com.seidor.data.api

import com.seidor.data.model.UserApi
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<UserApi>
}