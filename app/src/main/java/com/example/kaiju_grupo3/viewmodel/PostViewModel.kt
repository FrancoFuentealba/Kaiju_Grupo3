package com.example.kaiju_grupo3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju_grupo3.model.Post
import com.example.kaiju_grupo3.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                println("Error al obtener datos: ${e.localizedMessage}")
            }
        }
    }

    fun createPost(title: String, body: String) {
        viewModelScope.launch {
            try {
                val creado = repository.createPost(Post(userId = 1, id = 0, title = title, body = body))
                _postList.value = listOf(creado) + _postList.value
            } catch (e: Exception) {
                println("Error al crear: ${e.localizedMessage}")
            }
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            try {
                val editado = repository.updatePost(post.id, post.copy(title = post.title + " (editado)"))
                _postList.value = _postList.value.map { if (it.id == post.id) editado else it }
            } catch (e: Exception) {
                println("Error al actualizar: ${e.localizedMessage}")
            }
        }
    }

    fun deletePost(id: Int) {
        viewModelScope.launch {
            try {
                repository.deletePost(id)
                _postList.value = _postList.value.filter { it.id != id }
            } catch (e: Exception) {
                println("Error al eliminar: ${e.localizedMessage}")
            }
        }
    }
}