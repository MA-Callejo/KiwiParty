package com.kiwistudio.kiwiparty.pantallas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kiwistudio.kiwiparty.MainViewModel
import com.kiwistudio.kiwiparty.webservice.Pregunta

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Game(navController: NavController) {
    val viewModel: MainViewModel = viewModel()
    viewModel.getPreguntas()
    var preguntaIndex by remember{ mutableIntStateOf(0) }
    val preguntas by viewModel.preguntas.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        viewModel.getPreguntas()
    }
    if(preguntas.isNotEmpty()){
        val preguntaActual = preguntas[preguntaIndex]
        Text(
            text = when(preguntaActual.tipo){
                1 -> "Yo nunca..."
                2 -> "Verdad o reto"
                3 -> "¿Que prefieres?"
                4 -> "Desafío"
                else -> "???"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E), // Color oscuro para texto principal
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        AnimatedContent(
            targetState = preguntaActual,
            transitionSpec = {
                slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
            }
        ) { targetPregunta ->
            PreguntaCard(targetPregunta, {valor ->
                preguntaIndex += 1
            })
        }
    }
}

@Composable
fun PreguntaCard(
    pregunta: Pregunta, // Objeto pregunta
    onNextClick: (valor: Int?) -> Unit, // Acción al hacer clic en "Siguiente pregunta"
) {
    var valoracion: Int? by remember { mutableStateOf(null) }

    // Layout de la tarjeta
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(contentColor = Color(0xFFFFF3E0)) // Color cálido de fondo de la Card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()){
                Column(modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center){
                    // Mostrar texto1 (Obligatorio)
                    Text(
                        text = when(pregunta.tipo){
                            1 -> "Yo nunca..."
                            2 -> "Verdad"
                            3 -> "A)"
                            4 -> "Desafío"
                            else -> "???"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4E342E), // Color oscuro para texto principal
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = pregunta.texto1,
                        fontSize = 16.sp,
                        color = Color(0xFF757575), // Texto secundario en gris
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                // Mostrar texto2 (Opcional)
                pregunta.texto2?.let {
                    Column(modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center){
                        Text(
                            text = when(pregunta.tipo){
                                2 -> "Reto"
                                3 -> "B)"
                                else -> "???"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E), // Color oscuro para texto principal
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = it,
                            fontSize = 16.sp,
                            color = Color(0xFF757575), // Texto secundario en gris
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Botones de valoración
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de valoración positiva
                Button(
                    onClick = { valoracion = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (valoracion == 1) {
                            Color(0xFFFF6F61) // Color principal (naranja) al seleccionar
                        } else {
                            Color(0xFFBDBDBD) // Gris cuando no está seleccionado
                        }
                    ),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text(text = "👍", color = Color.White)
                }

                // Botón de valoración negativa
                Button(
                    onClick = { valoracion = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (valoracion == 0) {
                            Color(0xFFD84315) // Rojo cuando está seleccionado
                        } else {
                            Color(0xFFBDBDBD) // Gris cuando no está seleccionado
                        }
                    ),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text(text = "👎", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para pasar a la siguiente pregunta
            Button(
                onClick = { onNextClick(valoracion) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6F61) // Botón con color naranja festivo
                ),
                shape = RoundedCornerShape(50) // Botón con bordes redondeados
            ) {
                Text(text = "Siguiente pregunta", color = Color.White)
            }
        }
    }
}

@Preview
@Composable
fun PreguntaCardPreview(){
    PreguntaCard(Pregunta(id = 1, texto1 = "A que no", texto2 = "No lo se", tipo = 2)) { }
}