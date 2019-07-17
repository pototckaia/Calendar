package com.example.calendar.repository.server

import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*


const val request = "/api/v1"

interface PlannerApi {

    // iCal
    @GET("$request/export")
    @Streaming
    fun exportICal() : Observable<ResponseBody>

    @POST("$request/export")
    @Multipart
    @Headers(
        "Content-Type: multipart/form-data",
        "accept: application/json")
    fun importICal(@Part file: MultipartBody.Part) : Completable

    // Event
    @GET("$request/events")
    fun getEventsOffset(
        @Query("count") count: Int = 100,
        @Query("offset") offset: Long = 0
//        @Query("owner_id") owner_id: String,
//        @Query("id") id: List<Long>,
//        @Query("from") from: Long,
//        @Query("to") to: Long,
//        @Query("created_from") created_from: Long,
//        @Query("created_to") created_to: Long,
//        @Query("updated_from") updated_from: Long,
//        @Query("updated_to") updated_to: Long
    ) : Observable<EventResponse>

    // [from, to]
    @GET("$request/events")
    fun getEventsFromTo(
        @Query("from") from: Long,
        @Query("to") to: Long
    ) : Observable<EventResponse>

    @POST("$request/events")
    fun createEvent(
        @Body event: EventRequest
    ): Observable<EventResponse>

    @GET("$request/events/{id}")
    fun getEventById(@Path("id") id: Long) : Observable<EventResponse>

    @DELETE("$request/events/{id}")
    fun deleteEventById(@Path("id") id: Long) : Observable<EventResponse>

    @PATCH("$request/events/{id}")
    fun updateEvent(
        @Path("id") id: Long,
        @Body updates: EventRequest
    ) : Observable<EventResponse>

    // [from, to]
    @GET("$request/events/instances")
    fun getEventsInstancesFromTo(
        @Query("from") from: Long,
        @Query("to") to: Long
//        @Query("owner_id") owner_id: String,
//        @Query("count") count: Int = 100,
//        @Query("offset") offset: Long = 0
//        @Query("id") id: List<Long>,
//        @Query("created_from") created_from: Long,
//        @Query("created_to") created_to: Long,
//        @Query("updated_from") updated_from: Long,
//        @Query("updated_to") updated_to: Long
    ) : Observable<EventInstanceResponse>


    // Pattern
    @GET("$request/patterns")
    fun getPatterns(
        @Query("event_id") event_id: Long
    ) : Observable<EventPatternResponse>

    @GET("$request/patterns")
    fun getPatternsFromTo(
        @Query("from") from: Long,
        @Query("to") to: Long
    ) : Observable<EventPatternResponse>

    @POST("$request/patterns")
    fun createPattern(
        @Query("event_id") event_id: Long,
        @Body pattern: PatternRequest
    ): Observable<EventPatternResponse>

    @GET("$request/patterns/{id}")
    fun getPatternById(@Path("id") id: Long) : Observable<EventPatternResponse>

    @DELETE("$request/patterns/{id}")
    fun deletePatternById(@Path("id") id: Long) : Observable<EventPatternResponse>

    @PATCH("$request/patterns/{id}")
    fun updatePattern(
        @Path("id") id: Long,
        @Body updates: PatternRequest
    ) : Observable<EventPatternResponse>

    // Permission

    // Grant permission to user for specific entity
    @GET("$request/grant")
    fun getGrant(
        @Query("action") action: PermissionAction,
        @Query("entity_id") entity_id: Long?, //  Grant all entities of requested type if not set
        @Query("entity_type") entity_type: EntityType, // EVENT, PATTERN, TASK
        @Query("user_id") user_id: String // READ, UPDATE, DELETE
    ) : Observable<PermissionResponse>

    // Get granted permission for resources
    @GET("$request/permissions")
    fun getPermissions(
    	@Query("entity_type") entity_type: EntityType, // Get only entities of specified type
    	@Query("mine") meni: Boolean = true,
        @Query("count") count: Int = 100,
        @Query("offset") offset: Long = 0
    ) : Observable<PermissionResponse>

    @DELETE("$request/permissions/{id}")
    fun revokePermissionById(@Path("id") id: Long) : Observable<PermissionResponse>

    // Generate a link for sharing permission on specific entity
    @GET("$request/share")
    fun getLink(
        @Query("action") action: PermissionAction,
        @Query("entity_id") entity_id: Long?,
        @Query("entity_type") entity_type: EntityType
    ) : Observable<String>

    // Generate a link for sharing multiple permissions
    @POST("$request/share")
    fun getLink(
        @Body permissions: List<PermissionRequest>
    ) : Observable<ResponseBody>

    // Activate generated share-link
    @GET("$request/share/{token}")
    fun activateLink(@Path("token") token: String) : Observable<PermissionResponse>

    // Find user
    @GET("$request/user")
    fun getUser(
    	@Query("user_id") user_id: String?,
    	@Query("phone") phone: String?,
    	@Query("email") email: String?) : Observable<UserResponse>


    // TaskServer

    @GET("$request/tasks")
    fun getTasksByEventId(
        @Query("event_id") event_id: Long
//        @Query("count") count: Int = 100,
//        @Query("offset") offset: Long = 0,
//        @Query("created_from") created_from: Long,
//        @Query("created_to") created_to: Long,
//        @Query("id") id: List<Long>,
//        @Query("updated_from") updated_from: Long,
//        @Query("updated_to") updated_to: Long
    ) : Observable<TaskResponse>

    @POST("$request/tasks")
    fun createTasks(
        @Query("event_id") event_id: Long,
        @Body task : TaskRequest
    ): Observable<TaskResponse>

    @GET("$request/tasks/{id}")
    fun getTaskById(@Path("id") id: Long) : Observable<TaskResponse>

    @DELETE("$request/tasks/{id}")
    fun deleteTaskById(@Path("id") id: Long) : Observable<TaskResponse>

    @PATCH("$request/tasks/{id}")
    fun updateTask(
        @Path("id") id: Long,
        @Body updates: TaskRequest
    ) : Observable<TaskResponse>

}
