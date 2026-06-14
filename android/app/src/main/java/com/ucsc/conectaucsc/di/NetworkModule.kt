package com.ucsc.conectaucsc.di

import com.ucsc.conectaucsc.data.remote.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.ucsc.conectaucsc.data.remote.MateriaApiService
import android.content.Context
import com.ucsc.conectaucsc.data.remote.AsistenciasApiService
import com.ucsc.conectaucsc.data.remote.HorariosApiService
import com.ucsc.conectaucsc.data.remote.LogrosApiService
import com.ucsc.conectaucsc.data.remote.NotasApiService
import com.ucsc.conectaucsc.data.remote.RegistroApiService
import com.ucsc.conectaucsc.utils.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import com.ucsc.conectaucsc.data.remote.SesionApiService

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
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMateriaApiService(retrofit: Retrofit): MateriaApiService {
        return retrofit.create(MateriaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAsistenciasApiService(retrofit: Retrofit): AsistenciasApiService {
        return retrofit.create(AsistenciasApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHorariosApiService(retrofit: Retrofit): HorariosApiService {
        return retrofit.create(HorariosApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLogrosApiService(retrofit: Retrofit): LogrosApiService {
        return retrofit.create(LogrosApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotasApiService(retrofit: Retrofit): NotasApiService {
        return retrofit.create(NotasApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegistroApiService(retrofit: Retrofit): RegistroApiService {
        return retrofit.create(RegistroApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSesionApiService(retrofit: Retrofit): SesionApiService {
        return retrofit.create(SesionApiService::class.java)
    }
}