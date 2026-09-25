package com.avanade.devnews.di

import com.avanade.devnews.data.repository.AuthRepositoryImpl
import com.avanade.devnews.data.repository.NewsRepositoryImpl
import com.avanade.devnews.domain.repository.AuthRepository
import com.avanade.devnews.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(newsRepositoryImpl: NewsRepositoryImpl): NewsRepository
}
