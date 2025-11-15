package com.seidor.myapplication.di

import com.seidor.myapplication.ui.users.UserViewModel
import com.seidor.myapplication.ui.users.details.UserDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { UserViewModel(get(), get(), get()) }
    viewModel { UserDetailsViewModel(get()) }
}
