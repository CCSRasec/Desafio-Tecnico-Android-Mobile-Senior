package com.seidor.domain.di

import com.seidor.domain.usecase.GetUserDetailUseCase
import com.seidor.domain.usecase.GetUsersPageUseCase
import com.seidor.domain.usecase.GetUsersUseCase
import com.seidor.domain.usecase.RefreshUsersUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetUsersUseCase(get()) }
    factory { GetUsersPageUseCase(get()) }
    factory { RefreshUsersUseCase(get()) }
    factory { GetUserDetailUseCase(get()) }
}
