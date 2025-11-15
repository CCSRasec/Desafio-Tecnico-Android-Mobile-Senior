package com.seidor.data.di

import androidx.room.Room
import com.seidor.data.BuildConfig
import com.seidor.data.api.ApiService
import com.seidor.data.local.AppDatabase
import com.seidor.data.repository.UserRepositoryImpl
import com.seidor.domain.repository.UserRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val dataModule = module {

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.URL_API)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "users.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().userDao() }

    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}