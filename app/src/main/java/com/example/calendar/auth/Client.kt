package com.example.calendar.auth

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import rx.schedulers.Schedulers
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level


class Client private constructor() {

    private val baseUrl = "planner.skillmasters.ga/"
    internal var api: PlannerService

    init {
        val logging = HttpLoggingInterceptor()
        logging.level =  Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io())

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(rxAdapter)
            .build()

        api = retrofit.create(PlannerService::class.java)
    }

    companion object {
        val instance = Client()
    }
}