package com.skoove.challenge.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skoove.challenge.data.BuildConfig
import com.skoove.challenge.data.api.AudioEntryApi
import com.skoove.challenge.data.repository.AudioEntryRepository
import com.skoove.challenge.data.repository.AudioEntryRepositoryImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

object Modules {
    val common = module {
        single {
            createJson()
        }
        single {
            createHttpClient()
        }
        single {
            createRetroFit(json = get(),
                           okHttpClient = get())
        }
        factory { createAudioEntryApi(get()) }
        factory<AudioEntryRepository> { AudioEntryRepositoryImpl(get()) }
    }


    @OptIn(ExperimentalSerializationApi::class)
    private fun createJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createRetroFit(json: Json, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.CONNECTION_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private fun createAudioEntryApi(retrofit: Retrofit): AudioEntryApi = retrofit.create(AudioEntryApi::class.java)

    @Suppress("KotlinConstantConditions")
    private fun createHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.BUILD_TYPE != "release") {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    })
                }
            }.build()

}

