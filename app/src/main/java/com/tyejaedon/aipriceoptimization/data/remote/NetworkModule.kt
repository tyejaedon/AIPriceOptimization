package com.tyejaedon.aipriceoptimization.data.remote

import com.tyejaedon.aipriceoptimization.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * Provides the Retrofit/OkHttp networking stack.
 *
 * The base URL is supplied per build flavor (dev/staging/production) via
 * BuildConfig.API_BASE_URL - see app/build.gradle.kts product flavors and
 * docs/Release_Runbook.md for how to change it per environment.
 *
 * Logging never includes request/response bodies outside debug builds, and
 * the Authorization header is redacted once the auth interceptor lands in
 * Phase 2 (see docs/Mobile_Blueprint.md, section "Security design").
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // TODO(Phase 2): add FirebaseTokenInterceptor here once Firebase
        // Authentication is wired in.
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun providePricingApi(retrofit: Retrofit): PricingApi =
        retrofit.create(PricingApi::class.java)
}

