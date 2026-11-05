package com.example.kaiju_grupo3.remote

import com.example.kaiju_grupo3.model.Post
import retrofit2.http.GET

interface ApiService {

    @GET("/posts")
    suspend fun getPosts(): List<Post>
}
