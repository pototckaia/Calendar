package com.example.calendar.repository.server

import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*


const val request = "/api/v1"

interface PlannerApi {

    // iCal
    @GET("$request/export")
    @Streaming
    fun exportICal() : Single<ResponseBody>


    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    @Multipart
    @POST("$request/import")
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
    ) : Single<EventResponse>

    @POST("$request/events")
    fun createEvent(
        @Body event: EventRequest
    ): Single<EventResponse>

    @GET("$request/events/{id}")
    fun getEventById(@Path("id") id: Long) : Single<EventResponse>

    @DELETE("$request/events/{id}")
    fun deleteEventById(@Path("id") id: Long) : Single<EventResponse>

    @PATCH("$request/events/{id}")
    fun updateEvent(
        @Path("id") id: Long,
        @Body updates: EventRequest
    ) : Single<EventResponse>

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
    ) : Single<EventInstanceResponse>


    // Pattern
    @GET("$request/patterns")
    fun getPatterns(
        @Query("event_id") event_id: Long
    ) : Single<EventPatternResponse>

    @GET("$request/patterns")
    fun getPatternsFromTo(
        @Query("from") from: Long,
        @Query("to") to: Long
    ) : Single<EventPatternResponse>

    @POST("$request/patterns")
    fun createPattern(
        @Query("event_id") event_id: Long,
        @Body pattern: PatternRequest
    ): Single<EventPatternResponse>

    @GET("$request/patterns/{id}")
    fun getPatternById(@Path("id") id: Long) : Single<EventPatternResponse>

    @DELETE("$request/patterns/{id}")
    fun deletePatternById(@Path("id") id: Long) : Single<EventPatternResponse>

    @PATCH("$request/patterns/{id}")
    fun updatePattern(
        @Path("id") id: Long,
        @Body updates: PatternRequest
    ) : Single<EventPatternResponse>

    // Permission

    // Grant permission to user for specific entity
    @GET("$request/grant")
    fun getGrant(
        @Query("action") action: PermissionAction,
        @Query("entity_id") entity_id: Long?, //  Grant all entities of requested type if not set
        @Query("entity_type") entity_type: EntityType, // EVENT, PATTERN, TASK
        @Query("user_id") user_id: String // READ, UPDATE, DELETE
    ) : Single<PermissionResponse>

    // Get granted permission for resources
    @GET("$request/permissions")
    fun getPermissions(
    	@Query("entity_type") entity_type: EntityType, // Get only entities of specified type
    	@Query("mine") meni: Boolean = true,
        @Query("count") count: Int = 100,
        @Query("offset") offset: Long = 0
    ) : Single<PermissionResponse>

    // Get granted permission for resources
    @GET("$request/permissions")
    fun getPermissionsById(
        @Query("entity_type") entity_type: EntityType,
    	@Query("id") id: List<Long>
    ) : Single<PermissionResponse>

    @DELETE("$request/permissions/{id}")
    fun revokePermissionById(@Path("id") id: Long) : Single<PermissionResponse>

    // Generate a link for sharing permission on specific entity
    @GET("$request/share")
    fun getLink(
        @Query("action") action: PermissionAction,
        @Query("entity_id") entity_id: Long?,
        @Query("entity_type") entity_type: EntityType
    ) : Single<String>

    // Generate a link for sharing multiple permissions
    @POST("$request/share")
    fun getLink(
        @Body permissions: List<PermissionRequest>
    ) : Single<ResponseBody>

    // Activate generated share-link
    @GET("$request/share/{token}")
    fun activateLink(@Path("token") token: String) : Single<PermissionResponse>

    // Find user
    @GET("$request/user")
    fun getUser(
    	@Query("user_id") user_id: String?,
    	@Query("phone") phone: String?,
    	@Query("email") email: String?) : Single<UserResponse>


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
    ) : Single<TaskResponse>

    @POST("$request/tasks")
    fun createTasks(
        @Query("event_id") event_id: Long,
        @Body task : TaskRequest
    ): Single<TaskResponse>

    @GET("$request/tasks/{id}")
    fun getTaskById(@Path("id") id: Long) : Single<TaskResponse>

    @DELETE("$request/tasks/{id}")
    fun deleteTaskById(@Path("id") id: Long) : Single<TaskResponse>

    @PATCH("$request/tasks/{id}")
    fun updateTask(
        @Path("id") id: Long,
        @Body updates: TaskRequest
    ) : Single<TaskResponse>

}
