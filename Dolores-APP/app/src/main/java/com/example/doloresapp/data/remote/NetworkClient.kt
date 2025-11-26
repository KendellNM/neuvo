package com.example.doloresapp.data.remote

import android.content.Context
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.repository.AuthTokenRepositoryImpl
import com.example.doloresapp.data.remote.interceptor.AuthInterceptor
import com.example.doloresapp.domain.repository.AuthTokenRepository
import com.example.doloresapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        TokenStore.init(context.applicationContext)

        val tokenRepo: AuthTokenRepository = AuthTokenRepositoryImpl()
        val authInterceptor = AuthInterceptor(tokenProvider = { tokenRepo.getAuthToken() })

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        val instance = retrofit ?: error("NetworkClient no inicializado. Llama a init(context)")
        return instance.create(service)
    }
}

