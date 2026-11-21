package com.example.kaiju_grupo3.repository

import com.example.kaiju_grupo3.model.Post
import com.example.kaiju_grupo3.remote.RetrofitInstance
import retrofit2.Response

class PostRepository {

    suspend fun getPosts(): List<Post> = RetrofitInstance.api.getPosts()

    suspend fun createPost(post: Post): Post = RetrofitInstance.api.createPost(post)

    suspend fun updatePost(id: Int, post: Post): Post = RetrofitInstance.api.updatePost(id, post)

    suspend fun deletePost(id: Int): Response<Unit> = RetrofitInstance.api.deletePost(id)
}