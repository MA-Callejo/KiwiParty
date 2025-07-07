package com.kiwistudio.kiwiparty.pantallas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kiwistudio.kiwiparty.MainViewModel
import com.kiwistudio.kiwiparty.R
import com.kiwistudio.kiwiparty.webservice.Pregunta

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Game(navController: NavController, viewModel: MainViewModel) {
    var preguntaIndex by remember { mutableIntStateOf(0) }
    val porcentajes by viewModel.porcentajes.observeAsState(Pair(0, 0))
    val preguntas by viewModel.preguntas.observeAsState(emptyList())
    var nombreJugador by remember { mutableStateOf(viewModel.getRandomPlayer()) }
    LaunchedEffect(Unit) {
        viewModel.getPreguntas()
    }
    if (preguntas.isNotEmpty()) {
        val preguntaActual = preguntas[preguntaIndex]
        Column(
            modifier = Modifier
                .background(Color(0xFFFFECB3))
                .fillMaxSize()
        ) {
            Text(
                text = when (preguntaActual.tipo) {
                    1 -> stringResource(R.string.yo_nunca)
                    2 -> stringResource(R.string.verdad_o_reto)
                    3 -> stringResource(R.string.que_preferir_as)
                    4 -> stringResource(R.string.desaf_os)
                    else -> "???"
                },
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9D2209),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 60.dp)
                    .fillMaxWidth()
            )
            AnimatedContent(
                targetState = preguntaActual,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                }
            ) { targetPregunta ->
                PreguntaCard(targetPregunta, onNextClick =  { valor ->
                    nombreJugador = viewModel.getRandomPlayer()
                    if (preguntaIndex + 1 < preguntas.size) {
                        preguntaIndex += 1
                    } else {
                        viewModel.shufflePreguntas()
                        preguntaIndex = 0
                    }
                }, onValorar = { valor -> viewModel.evaluar(preguntaActual.id, valor)},
                    porcentaje1 = porcentajes.first,
                    porcentaje2 = porcentajes.second,
                    nombreJugador = nombreJugador)
            }
        }
    }
}

@Composable
fun PreguntaCard(pregunta: Pregunta, onNextClick: (valor: Int?) -> Unit, onValorar: (valor: Int) -> Unit, porcentaje1: Int, porcentaje2: Int, nombreJugador: String) {
    var valoracion: Int? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(contentColor = Color(0xFFFFECB3))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    when (pregunta.tipo) {
                        1, 4 -> {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = pregunta.texto1,
                                    fontSize = 16.sp,
                                    color = Color(0xFF4E342E),
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        2 -> {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = nombreJugador,
                                    fontSize = 24.sp,
                                    color = Color(0xFF9D2209),
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    textAlign = TextAlign.Center
                                )
                                FlippingCardsRow(pregunta.texto1, pregunta.texto2 ?: "")
                            }
                        }
                        3 -> {
                            FlippingCardsPercentRow(pregunta.texto1, pregunta.texto2 ?: "", porcentaje1, porcentaje2, {valor -> onValorar(valor)})
                        }
                    }
                }
                Button(
                    onClick = { onNextClick(valoracion) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F61)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = stringResource(R.string.siguiente_pregunta), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FlippingCardsRow(texto1: String, texto2: String) {
    var selectedCardIndex by remember { mutableStateOf<Int?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FlippingCard(
            texto1,
            selectedCardIndex != 0,
            true,
            selectedCardIndex == 0,
            selectedCardIndex == 0 || selectedCardIndex == null
        ) {
            selectedCardIndex = if (selectedCardIndex == 0) null else 0
        }
        FlippingCard(
            texto2,
            selectedCardIndex != 1,
            false,
            selectedCardIndex == 1,
            selectedCardIndex == 1 || selectedCardIndex == null
        ) {
            selectedCardIndex = if (selectedCardIndex == 1) null else 1
        }
    }
}

@Composable
fun FlippingCard(texto: String, pulsable: Boolean, verdad: Boolean, isSelected: Boolean, visible: Boolean, onClick: () -> Unit) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotationYval by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    // Cambiar el tamaño de la tarjeta de manera que no interrumpa la animación
    val cardSize by animateDpAsState(
        targetValue = if(visible) if (isSelected) 300.dp else 140.dp else 1.dp,
        animationSpec = tween(durationMillis = 600)
    )

    val frontVisible = rotationYval <= 90f || rotationYval >= 270f

    Card(
        modifier = Modifier
            .width(cardSize)
            .height(cardSize)
            .aspectRatio(1f)
            .graphicsLayer {
                rotationY = rotationYval
                cameraDistance = 12f * density
            }
            .padding(5.dp)
            .clickable {
                if (pulsable) {
                    isFlipped = !isFlipped
                    onClick()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (verdad)
                if (frontVisible) Color(0xFF315D2C) else Color(0xFF8BEA80)
            else
                if (frontVisible) Color(0xFF5D2C2C) else Color(0xFFEA8080)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (frontVisible) {
                Text(
                    text = if (verdad) stringResource(R.string.verdad) else stringResource(R.string.reto),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = texto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Composable
fun FlippingCardsPercentRow(texto1: String, texto2: String, porcentaje1: Int, porcentaje2: Int, onClick: (valor: Int) -> Unit){
    var fliped by remember{ mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FlippingCardsPercent(texto1, porcentaje2, fliped) {
            onClick(0)
            fliped = true
        }
        FlippingCardsPercent(texto2, porcentaje1, fliped) {
            onClick(1)
            fliped = true
        }
    }
}

@Composable
fun FlippingCardsPercent(texto: String, porcentaje: Int, fliped: Boolean, onClick: () -> Unit){
    val rotationYval by animateFloatAsState(
        targetValue = if (fliped) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    val frontVisible = rotationYval <= 90f || rotationYval >= 270f

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(140.dp)
            .aspectRatio(1f)
            .graphicsLayer {
                rotationY = rotationYval
                cameraDistance = 12f * density
            }
            .padding(5.dp)
            .clickable {
                if (!fliped) {
                    onClick()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (frontVisible)
                Color(0xFF404040)
            else
                if (porcentaje > 50) Color(0xFF8BEA80) else Color(0xFFEA8080)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (frontVisible) {
                Text(
                    text = texto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "$porcentaje%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreguntaCardPreview(){
    //PreguntaCard(Pregunta(id = 1, texto1 = "A que no", texto2 = "No lo se", tipo = 2), onNextClick = { }, onValorar = {}, porcentaje2 = 0, porcentaje1 = 0)
}