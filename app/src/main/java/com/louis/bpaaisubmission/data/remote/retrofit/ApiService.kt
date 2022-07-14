package com.louis.bpaaisubmission.data.remote.retrofit

import com.louis.bpaaisubmission.data.remote.response.LoginResponse
import com.louis.bpaaisubmission.data.remote.response.RegisterResponse
import com.louis.bpaaisubmission.data.remote.response.StoriesResponse
import com.louis.bpaaisubmission.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page:Int? = null,
        @Query("size") size:Int? = null,
        @Query("location") location: Int? = null
    ) : StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
        @Part photo: MultipartBody.Part
    ) : UploadResponse
}