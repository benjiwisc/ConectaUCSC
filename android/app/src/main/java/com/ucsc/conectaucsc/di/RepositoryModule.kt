package com.ucsc.conectaucsc.di

import com.ucsc.conectaucsc.data.remote.*
import com.ucsc.conectaucsc.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    @Provides
    @Singleton
    fun provideSesionRepository(api: SesionApiService): SesionRepository {
        return SesionRepository(api)
    }

    @Provides
    @Singleton
    fun provideEvaluacionesPracticasRepository(api: EvaluacionesPracticasApiService): EvaluacionesPracticasRepository {
        return EvaluacionesPracticasRepository(api)
    }

    @Provides
    @Singleton
    fun provideArchivosEstudioRepository(api: ArchivosEstudioApiService): ArchivosEstudioRepository {
        return ArchivosEstudioRepository(api)
    }
}