package com.kiwistudio.kiwiparty.webservice

data class Pregunta (
    val id: Int,
    val tipo: Int,
    var texto1: String,
    var texto2: String?,
)

data class ApiResponse(
    val status: String,
    val preguntas: List<Pregunta>
)