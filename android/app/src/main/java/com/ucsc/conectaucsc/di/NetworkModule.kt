package com.ucsc.conectaucsc.di

import android.content.Context
import com.ucsc.conectaucsc.data.remote.*
import com.ucsc.conectaucsc.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val token = sessionManager.getToken()
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideMateriaApiService(retrofit: Retrofit): MateriaApiService = retrofit.create(MateriaApiService::class.java)

    @Provides
    @Singleton
    fun provideAsistenciasApiService(retrofit: Retrofit): AsistenciasApiService = retrofit.create(AsistenciasApiService::class.java)

    @Provides
    @Singleton
    fun provideHorariosApiService(retrofit: Retrofit): HorariosApiService = retrofit.create(HorariosApiService::class.java)

    @Provides
    @Singleton
    fun provideLogrosApiService(retrofit: Retrofit): LogrosApiService = retrofit.create(LogrosApiService::class.java)

    @Provides
    @Singleton
    fun provideNotasApiService(retrofit: Retrofit): NotasApiService = retrofit.create(NotasApiService::class.java)

    @Provides
    @Singleton
    fun provideRegistroApiService(retrofit: Retrofit): RegistroApiService = retrofit.create(RegistroApiService::class.java)

    @Provides
    @Singleton
    fun provideSesionApiService(retrofit: Retrofit): SesionApiService = retrofit.create(SesionApiService::class.java)

    @Provides
    @Singleton
    fun provideEvaluacionesPracticasApiService(retrofit: Retrofit): EvaluacionesPracticasApiService = retrofit.create(EvaluacionesPracticasApiService::class.java)

    @Provides
    @Singleton
    fun provideArchivosEstudioApiService(retrofit: Retrofit): ArchivosEstudioApiService = retrofit.create(ArchivosEstudioApiService::class.java)
}