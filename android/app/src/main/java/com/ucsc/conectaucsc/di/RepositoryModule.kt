package com.ucsc.conectaucsc.di

import com.ucsc.conectaucsc.data.remote.AuthApiService
import com.ucsc.conectaucsc.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.ucsc.conectaucsc.data.remote.MateriaApiService
import com.ucsc.conectaucsc.data.repository.MateriaRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApiService): AuthRepository {
        return AuthRepository(api)
    }

    @Provides
    @Singleton
    fun provideMateriaRepository(api: MateriaApiService): MateriaRepository {
        return MateriaRepository(api)
    }
}