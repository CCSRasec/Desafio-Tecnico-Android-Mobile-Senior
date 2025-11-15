package com.seidor.domain.usecase

import com.seidor.domain.model.User
import com.seidor.domain.repository.UserRepository

class GetUserDetailUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int): User? {
        return repository.getUserById(userId)
    }
}