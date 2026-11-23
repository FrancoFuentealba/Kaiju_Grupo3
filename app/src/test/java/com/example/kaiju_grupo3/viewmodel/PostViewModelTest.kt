package com.example.kaiju_grupo3.viewmodel

import com.example.kaiju_grupo3.MainDispatcherRule
import com.example.kaiju_grupo3.model.Post
import com.example.kaiju_grupo3.repository.PostRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repo = mockk<PostRepository>()

    private val post1 = Post(userId = 1, id = 1, title = "Título 1", body = "Contenido 1")
    private val post2 = Post(userId = 2, id = 2, title = "Título 2", body = "Contenido 2")

    @Test
    fun `postList contiene los datos esperados despues de cargar`() = runTest {
        coEvery { repo.getPosts() } returns listOf(post1, post2)

        val vm = PostViewModel(repo)

        assertEquals(listOf(post1, post2), vm.postList.value)
    }

    @Test
    fun `si la carga falla postList queda vacia`() = runTest {
        coEvery { repo.getPosts() } throws RuntimeException("sin red")

        val vm = PostViewModel(repo)

        assertTrue(vm.postList.value.isEmpty())
    }

    @Test
    fun `createPost agrega el post creado al inicio de la lista`() = runTest {
        coEvery { repo.getPosts() } returns listOf(post1)
        val creado = Post(userId = 1, id = 101, title = "Nuevo", body = "Cuerpo")
        coEvery { repo.createPost(any()) } returns creado
        val vm = PostViewModel(repo)

        vm.createPost("Nuevo", "Cuerpo")

        assertEquals(listOf(creado, post1), vm.postList.value)
        coVerify { repo.createPost(match { it.title == "Nuevo" && it.body == "Cuerpo" }) }
    }

    @Test
    fun `createPost con error no cambia la lista`() = runTest {
        coEvery { repo.getPosts() } returns listOf(post1)
        coEvery { repo.createPost(any()) } throws RuntimeException("falla")
        val vm = PostViewModel(repo)

        vm.createPost("Nuevo", "Cuerpo")

        assertEquals(listOf(post1), vm.postList.value)
    }

    @Test
    fun `updatePost reemplaza solo el post editado`() = runTest {
        coEvery { repo.getPosts() } returns listOf(post1, post2)
        val editado = post1.copy(title = "Título 1 (editado)")
        coEvery { repo.updatePost(1, any()) } returns editado
        val vm = PostViewModel(repo)

        vm.updatePost(post1)

        assertEquals(listOf(editado, post2), vm.postList.value)
        coVerify { repo.updatePost(1, match { it.title == "Título 1 (editado)" }) }
    }

    @Test
    fun `updatePost con error deja la lista igual`() = runTest {
        coEvery { repo.getPosts() } returns listOf(post1, post2)
        coEvery { repo.updatePost(any(), any()) } throws RuntimeException("falla")
        val vm = PostViewModel(repo)

        vm.updatePost(post1)

        assertEquals(listOf(post1, post2), vm.postList.value)
    }

    @Test
    fun `deletePost quita el post de la lista`() = runTest {
        coEvery { repo.getPosts() } returns listOf(post1, post2)
        coEvery { repo.deletePost(1) } returns Response.success(Unit)
        val vm = PostViewModel(repo)

        vm.deletePost(1)

        assertEquals(listOf(post2), vm.postList.value)
    }

    @Test
    fun `deletePost con error no quita nada`() = runTest {
        coEvery { repo.getPosts() } returns listOf(post1, post2)
        coEvery { repo.deletePost(1) } throws RuntimeException("falla")
        val vm = PostViewModel(repo)

        vm.deletePost(1)

        assertEquals(listOf(post1, post2), vm.postList.value)
    }
}