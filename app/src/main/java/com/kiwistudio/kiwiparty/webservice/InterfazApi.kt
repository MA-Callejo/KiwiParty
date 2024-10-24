package com.kiwistudio.kiwiparty.webservice
import retrofit2.Call
import retrofit2.http.*

interface InterfazApi {
    @GET("party/ws/getpreguntas.php")
    suspend fun getPreguntas(@Query("tipo") tipo: Int, @Query("adulto") adulto: Int, @Query("jugadores") jugadores: Int): ApiResponse

    @GET("party/ws/puntuar.php")
    suspend fun getPreguntas(@Query("id") id: Int, @Query("valoracion") valoracion: Int): ApiResponseValoracion
}