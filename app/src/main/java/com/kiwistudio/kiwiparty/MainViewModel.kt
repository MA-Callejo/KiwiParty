package com.kiwistudio.kiwiparty

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
import com.kiwistudio.kiwiparty.webservice.ApiResponse
import com.kiwistudio.kiwiparty.webservice.Pregunta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var jugadores: List<String> = listOf()
    var isOnlyAdult = false
    var modeSelected = 0
    private val _preguntas = MutableLiveData<List<Pregunta>>()
    val preguntas: LiveData<List<Pregunta>> get() = _preguntas
    val retrofit = Retrofit.Builder()
        .baseUrl("https://kiwistudio.great-site.net/") // Base URL de tu servidor
        .addConverterFactory(GsonConverterFactory.create()) // Convertidor para JSON
        .build()
    val service: InterfazApi = retrofit.create(InterfazApi::class.java)

    fun setJugadoresList(lista: List<String>){
        jugadores = lista
    }
    fun setAdultos(adulto: Boolean){
        isOnlyAdult = adulto
    }
    fun setTipo(tipo: Int){
        modeSelected = tipo
    }

    fun getPreguntas() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiResponse = service.getPreguntas(tipo = modeSelected, adulto = if(isOnlyAdult) 1 else 0)
                if (apiResponse.status == "success") {
                    var preguntasAux = apiResponse.preguntas.toMutableList()
                    preguntasAux.forEach { preg ->
                        jugadores.forEachIndexed { index, s ->
                            preg.texto1 = preg.texto1.replace("J$index", s)
                            if(preg.texto2 != null) {
                                preg.texto2 = preg.texto2!!.replace("J$index", s)
                            }
                        }
                    }
                    _preguntas.value = preguntasAux
                }
            } catch (e: Exception) {
                e.printStackTrace() // Manejar errores
                Log.e("GET", e.message?: "")
            }
        }
    }
}
