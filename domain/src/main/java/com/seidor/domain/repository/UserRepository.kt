package com.seidor.domain.repository

import com.seidor.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /** Observa a lista inteira local, com busca opcional. */
    fun observeUsers(query: String? = null): Flow<List<User>>

    /** Obtém uma página local (paginação simulada). */
    suspend fun getUsersPage(query: String? = null, limit: Int, offset: Int): List<User>

    /** Força refresh remoto -> persiste localmente. */
    suspend fun refreshUsers()

    /** Detalhe por id (local; faz sentido dar refresh antes em UI). */
    suspend fun getUserById(id: Int): User?
}