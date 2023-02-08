package com.nuai.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nuai.app.MyApplication
import com.nuai.network.NetworkService
import com.nuai.network.OkHttpClientFactory
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
        application: MyApplication,
        httpClientFactory: OkHttpClientFactory
    ): NetworkService {
        return NetworkService(context = application, okHttpClientFactory = httpClientFactory)
    }
}