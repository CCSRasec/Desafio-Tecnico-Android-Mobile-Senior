package com.seidor.domain.usecase

import com.seidor.domain.repository.UserRepository

class RefreshUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke() {
        repository.refreshUsers()
    }
}