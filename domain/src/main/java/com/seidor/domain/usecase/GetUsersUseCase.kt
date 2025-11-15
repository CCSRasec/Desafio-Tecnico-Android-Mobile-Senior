package com.seidor.domain.usecase

import com.seidor.domain.model.User
import com.seidor.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(query: String? = null): Flow<List<User>> {
        return repository.observeUsers(query)
    }
}
