package com.example.calendar.repository.server

import com.example.calendar.repository.server.convert.DurationJsonConvert
import com.example.calendar.repository.server.convert.ZoneIdJsonConvert
import com.example.calendar.repository.server.convert.ZonedDateTimeJsonConvert
import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import okhttp3.Headers
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


class Server private constructor() {

    companion object {
        val server = Server()
    }

    private val baseUrl = "http://planner.skillmasters.ga/"
    internal var api: PlannerApi

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

        val json = GsonBuilder()
            .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeJsonConvert())
            .registerTypeAdapter(Duration::class.java, DurationJsonConvert())
            .registerTypeAdapter(ZoneId::class.java, ZoneIdJsonConvert())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(json))
            .addCallAdapterFactory(rxAdapter)
            .build()

        api = retrofit.create(PlannerApi::class.java)
    }
}