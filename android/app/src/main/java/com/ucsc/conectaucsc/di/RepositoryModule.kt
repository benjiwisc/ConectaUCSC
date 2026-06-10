package com.ucsc.conectaucsc.di

import com.ucsc.conectaucsc.data.remote.AsistenciasApiService
import com.ucsc.conectaucsc.data.remote.AuthApiService
import com.ucsc.conectaucsc.data.remote.HorariosApiService
import com.ucsc.conectaucsc.data.remote.LogrosApiService
import com.ucsc.conectaucsc.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.ucsc.conectaucsc.data.remote.MateriaApiService
import com.ucsc.conectaucsc.data.remote.NotasApiService
import com.ucsc.conectaucsc.data.remote.RegistroApiService
import com.ucsc.conectaucsc.data.repository.AsistenciasRepository
import com.ucsc.conectaucsc.data.repository.HorariosRepository
import com.ucsc.conectaucsc.data.repository.LogrosRepository
import com.ucsc.conectaucsc.data.repository.MateriaRepository
import com.ucsc.conectaucsc.data.repository.NotasRepository
import com.ucsc.conectaucsc.data.repository.RegistroRepository

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

    @Provides
    @Singleton
    fun provideScheduleRepository(api: HorariosApiService): HorariosRepository {
        return HorariosRepository(api)
    }

    @Provides
    @Singleton
    fun provideGradeRepository(api: NotasApiService): NotasRepository {
        return NotasRepository(api)
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(api: AsistenciasApiService): AsistenciasRepository {
        return AsistenciasRepository(api)
    }

    @Provides
    @Singleton
    fun provideAttainmentRepository(api: LogrosApiService): LogrosRepository {
        return LogrosRepository(api)
    }

    @Provides
    @Singleton
    fun provideRecordRepository(api: RegistroApiService): RegistroRepository {
        return RegistroRepository(api)
    }
}