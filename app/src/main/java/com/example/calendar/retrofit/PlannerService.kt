package com.example.calendar.retrofit

import retrofit2.http.*
import rx.Observable


//Retrofit retrofit = new Retrofit.Builder()
//        .baseUrl("https://api.github.com/")
//        .build();
//
//        GitHubService service = retrofit.create(GitHubService.class);
const val request = "/api/v1"

internal interface PlannerService {

    //    Events
    @GET("$request/events")
    fun retrieve(@Query("ids") ids: List<Long>): Observable<List<Event>>

    @POST("$request/events")
    fun createEvent(@Body e: Event): Observable<List<Event>>

    @PUT("$request/events/{id}")
    fun updateEvent(@Path("id") id: Long, @Body e: Event) : Observable<List<Event>>

    @DELETE("$request/events/{id}")
    fun deleteEvent(@Path("id") id: Long): Void // ??? Complete

//    @GET("user")
//    @Headers({"", "", })
//    Call<User> getUser(@Header("Authorization") String authorization)

    //    EventPattern
    //    POST: api/v1/patterns/ -> ...
    //    GET: api/v1/patterns - pattern -> ...
    //    PUT: /api/v1/patterns/{id} - pattern body -> ...
    //    DELETE: /api/v1/patterns/{id} -> void

}
