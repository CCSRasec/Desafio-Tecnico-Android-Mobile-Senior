package com.seidor.domain.usecase

import com.seidor.domain.model.User
import com.seidor.domain.repository.UserRepository

class GetUsersPageUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        query: String? = null,
        limit: Int,
        offset: Int
    ): List<User> {
        return repository.getUsersPage(query, limit, offset)
    }
}