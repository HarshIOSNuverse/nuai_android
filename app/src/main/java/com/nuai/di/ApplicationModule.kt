package com.nuai.di

import android.app.Application
import com.nuai.network.NetworkService
import com.nuai.network.OkHttpClientFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    fun httpClient(): OkHttpClientFactory {
        return OkHttpClientFactory()
    }

    @Singleton
    @Provides
    fun provideNetworkService(
        application: Application,
        httpClientFactory: OkHttpClientFactory
    ): NetworkService {
        return NetworkService(context = application, okHttpClientFactory = httpClientFactory)
    }
}