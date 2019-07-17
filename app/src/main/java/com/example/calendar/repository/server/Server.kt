package com.example.calendar.repository.server

import android.content.Context
import com.example.calendar.helpers.USER_ID_TOKEN
import com.example.calendar.helpers.USER_ID_TOKEN_PREF
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import com.example.calendar.repository.server.convertJson.DurationJsonConvert
import com.example.calendar.repository.server.convertJson.ZoneIdJsonConvert
import com.example.calendar.repository.server.convertJson.ZonedDateTimeJsonConvert
import com.google.firebase.auth.FirebaseAuth
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
import retrofit2.converter.scalars.ScalarsConverterFactory


class Server {

    private val baseUrl = "http://planner.skillmasters.ga/"
    internal var api: PlannerApi

    init {
        val logging = HttpLoggingInterceptor()
        logging.level =  Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = InjectApplication.inject
                    .getSharedPreferences(USER_ID_TOKEN_PREF, Context.MODE_PRIVATE)
                    .getString(USER_ID_TOKEN, "")

                val originalRequest = chain.request()
                val headers = Headers.Builder()
                    .add("X-Firebase-Auth", token ?: "")
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
                    204 -> throw NoContent(response.message())
                    400 -> throw BadRequest(response.message())
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
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(rxAdapter)
            .build()

        api = retrofit.create(PlannerApi::class.java)
    }
}