package com.kiwistudio.kiwiparty

import SettingsRepository
import android.util.Log
import com.kiwistudio.kiwiparty.webservice.InterfazApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwistudio.kiwiparty.webservice.ApiResponse
import com.kiwistudio.kiwiparty.webservice.Pregunta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MainViewModel(private val repository: SettingsRepository) : ViewModel() {

    var jugadores: List<String> = listOf()
    val stringList: Flow<List<String>> = repository.stringListFlow
    var isOnlyAdult = false
    var modeSelected = 1
    private var preguntasval = mutableListOf<Pregunta>()
    private val _preguntas = MutableLiveData<List<Pregunta>>()
    val preguntas: LiveData<List<Pregunta>> get() = _preguntas
    private val _porcentajes = MutableLiveData<Pair<Int, Int>>()
    val porcentajes: LiveData<Pair<Int, Int>> get() = _porcentajes
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Nivel de log: BODY muestra toda la información (ruta, parámetros, etc.)
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://kiwiprojectstudio.com/") // Base URL de tu servidor
        .addConverterFactory(GsonConverterFactory.create()) // Convertidor para JSON
        .build()
    val service: InterfazApi = retrofit.create(InterfazApi::class.java)

    fun setJugadoresList(lista: List<String>){
        jugadores = lista
        saveStringList(jugadores)
    }
    fun setAdultos(adulto: Boolean){
        isOnlyAdult = adulto
    }
    fun setTipo(tipo: Int){
        modeSelected = tipo
    }
    fun saveStringList(stringList: List<String>) {
        viewModelScope.launch {
            repository.saveStringList(stringList)
        }
    }
    fun evaluar(id: Int, valoracion: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiResponse = service.getPreguntas(id=id, valoracion=valoracion)
                if (apiResponse.status == 100) {
                    val valor = apiResponse.valoracion
                    _porcentajes.postValue(Pair(valor, 100-valor))
                }
            } catch (e: Exception) {
                e.printStackTrace() // Manejar errores
                Log.e("GET", e.message?: "")
            }
        }
    }
    fun getPreguntas() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiResponse = service.getPreguntas(tipo = modeSelected+1, jugadores=jugadores.size, adulto = if(isOnlyAdult) 0 else 1)
                if (apiResponse.status == 100) {
                    preguntasval = apiResponse.preguntas.toMutableList()
                    shufflePreguntas()
                }
            } catch (e: Exception) {
                e.printStackTrace() // Manejar errores
                Log.e("GET", e.message?: "")
            }
        }
    }
    fun shufflePreguntas(){
        preguntasval.forEach { preg ->
            jugadores = jugadores.shuffled()
            jugadores.forEachIndexed { index, s ->
                preg.texto1 = preg.texto1.replace("J${index+1}", s)
                if(preg.texto2 != null) {
                    preg.texto2 = preg.texto2!!.replace("J${index+1}", s)
                }
            }
        }
        _preguntas.postValue(preguntasval)
    }
}
