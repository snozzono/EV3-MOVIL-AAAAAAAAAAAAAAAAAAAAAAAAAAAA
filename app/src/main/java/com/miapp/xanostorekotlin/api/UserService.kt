package com.miapp.xanostorekotlin.api

import com.miapp.xanostorekotlin.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UserService {
    @GET("user")
    suspend fun getUsers(): List<User>

    @GET("user/{user_id}")
    suspend fun getUser(@Path("user_id") id: Int): User

    @POST("user")
    suspend fun createUser(@Body request: CreateUserRequest): User

    @PUT("user/{user_id}")
    suspend fun updateUser(
        @Path("user_id") id: Int,
        @Body request: UpdateUserRequest
    ): User

    @PATCH("user/{user_id}")
    suspend fun patchUser(
        @Path("user_id") id: Int,
        @Body request: UpdateUserRequest
    ): User

    @DELETE("user/{user_id}")
    suspend fun deleteUser(@Path("user_id") id: Int)
}