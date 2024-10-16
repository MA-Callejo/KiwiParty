package com.kiwistudio.kiwiparty.webservice
import retrofit2.Call
import retrofit2.http.*

interface InterfazApi {
    @GET("party/ws/getpreguntas.php")
    suspend fun getPreguntas(@Query("tipo") tipo: Int, @Query("adulto") adulto: Int): ApiResponse
}