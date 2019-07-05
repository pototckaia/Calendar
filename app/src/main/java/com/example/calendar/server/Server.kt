package com.example.calendar.server

import io.reactivex.schedulers.Schedulers
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import okhttp3.Headers
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


class Server private constructor() {

    companion object {
        val server = Server()
    }

    private val baseUrl = "http://planner.skillmasters.ga/"
    internal var api: PlannerServiceApi

    init {
        val logging = HttpLoggingInterceptor()
        logging.level =  Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val headers = Headers.Builder()
                    .add("X-Firebase-Auth", "serega_mem")
                    .build()

                val newRequest = originalRequest.newBuilder()
                    .headers(headers)
                    .build()

                chain.proceed(newRequest)
            }
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                when (response.code()) {
                    204 -> throw NoContent()
                    400 -> throw BadRequest()
                    401 -> throw NotAuthorized()
                    403 -> throw AccessDenied()
                    404 -> throw NotFind()
                    500 -> throw InternalError()
                }
                response
            }
            .build()

        val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(rxAdapter)
            .build()

        api = retrofit.create(PlannerServiceApi::class.java)
    }
}