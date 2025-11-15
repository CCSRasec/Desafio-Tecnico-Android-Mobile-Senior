package com.seidor.data.repository

import com.seidor.data.api.ApiService
import com.seidor.data.local.dao.user.UserDao
import com.seidor.data.repository.mappers.toDomain
import com.seidor.data.repository.mappers.toEntity
import com.seidor.domain.model.User
import com.seidor.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class UserRepositoryImpl(
    private val api: ApiService,
    private val userDao: UserDao,
    private val cacheTtl: Duration = 30.minutes // ajuste fino: tempo de vida do cache
) : UserRepository {

    override fun observeUsers(query: String?): Flow<List<User>> =
        userDao.observeAll(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getUsersPage(
        query: String?, limit: Int, offset: Int
    ): List<User> = userDao.page(query, limit, offset).map { it.toDomain() }

    override suspend fun refreshUsers() {
        // Se já existe cache “recente”, pode pular chamada remota.
        // Aqui, vamos sempre tentar atualizar; se falhar, mantemos cache atual.
        val remote = api.getUsers()
        val now = System.currentTimeMillis()
        val entities = remote.map { it.toEntity(now) }

        // Estratégia simples: substituir todos para manter consistência com o jsonplaceholder
        userDao.clear()
        userDao.upsertAll(entities)
    }

    override suspend fun getUserById(id: Int): User? =
        userDao.getById(id)?.toDomain()
}
