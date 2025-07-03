package com.kiwistudio.kiwiparty.pantallas

import SettingsRepository
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kiwistudio.kiwiparty.MainViewModel
import com.kiwistudio.kiwiparty.R


@Composable
fun MainMenu(navController: NavController, viewModel: MainViewModel) {
    // Lista de modos de juego
    val gameModes = listOf(
        "Yo nunca" to R.drawable.mano, // Iconos representativos
        "Verdad o reto" to R.drawable.opciones,
        "¿Que preferirías?" to R.drawable.decision,
        "Desafíos" to R.drawable.objetivo,
        "Coctel" to R.drawable.tostada,
    )
    var modeSelected by remember { mutableIntStateOf(0) }
    var verDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) } // Inicializar el repositorio con el contexto
    var stringList by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        stringList = repository.getStringList() // Carga la lista desde el repositorio
    }
    if(verDialog){
        PlayerDialog(gameModes[modeSelected].first, {verDialog = false}, {lista, adulto ->
            verDialog = false
            repository.saveStringList(lista)
            viewModel.setJugadoresList(lista)
            viewModel.setAdultos(adulto)
            viewModel.setTipo(modeSelected)
            navController.navigate("newScreen")
        }, stringList)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFECB3)) // Color de fondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))
            // Nombre de la app (simulando un logo)
            Text(
                text = "Kiwi Party",
                modifier = Modifier.padding(16.dp),
                color = Color(0xFFD84315), // Color principal de los títulos
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            gameModes.forEachIndexed { index, (name, icon) ->
                GameModeCard(name, icon, {
                    modeSelected = index
                    verDialog = true
                })
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(){
                Text(fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    text = "¿Quieres ver tus propias preguntas en la app?")
                Text(
                    text = "Colabora",
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kiwiprojectstudio.com/party/index.php"))
                        context.startActivity(intent)
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Text(
            text = "v1.0",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp) // Añadir un pequeño padding desde los bordes
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDialog(
    gameModeName: String, // Nombre del modo de juego
    onDismiss: () -> Unit, // Acción al cerrar el dialog
    onStartGame: (List<String>, Boolean) -> Unit, // Acción al empezar el juego con jugadores y +18
    listaGuardad: List<String>
) {
    var players = remember { mutableStateListOf<String>() }
    var newPlayerName by remember { mutableStateOf(TextFieldValue("")) } // Nuevo jugador
    var isAdultOnly by remember { mutableStateOf(false) } // Selector +18
    LaunchedEffect(Unit) {
        players.clear()
        players.addAll(listaGuardad)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFFF3E0) // Fondo cálido de la tarjeta
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título del modo de juego
                Text(
                    text = gameModeName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E), // Color oscuro de los títulos
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Lista de jugadores
                players.forEachIndexed { index, playerName ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Nombre del jugador editable
                        TextField(
                            value = playerName,
                            onValueChange = { newName ->
                                players[index] = newName
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, capitalization = KeyboardCapitalization.Sentences),
                            keyboardActions = KeyboardActions.Default,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                focusedIndicatorColor = Color(0xFFFF6F61), // Color del borde cuando está en foco
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        // Botón para eliminar jugador
                        IconButton(onClick = { players.removeAt(index) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar jugador",
                                tint = Color(0xFFD84315) // Color del ícono de eliminar
                            )
                        }
                    }
                }

                // Campo de texto para añadir nuevos jugadores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nuevo jugador") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFFF6F61), // Color del borde cuando está en foco
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, capitalization = KeyboardCapitalization.Sentences),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (newPlayerName.text.isNotEmpty()) {
                                    players.add(newPlayerName.text)
                                    newPlayerName = TextFieldValue("") // Limpiar campo
                                }
                            }
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (newPlayerName.text.isNotEmpty()) {
                                players.add(newPlayerName.text)
                                newPlayerName = TextFieldValue("") // Limpiar campo
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F61))
                    ) {
                        Text(text = "Añadir", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de +18
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAdultOnly,
                        onCheckedChange = { isAdultOnly = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFF6F61),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Para menores de 18",
                        fontSize = 18.sp,
                        color = Color(0xFF4E342E)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para empezar el juego
                Button(
                    onClick = { onStartGame(players, isAdultOnly) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F61)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Empezar partida", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun GameModeCard(name: String, iconRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            // Icono representativo del modo de juego
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )

            // Nombre del modo de juego
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9D2209), // Color oscuro de los textos
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Icono de información
            /*Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                tint = Color(0xFF008AA1), // Color de los íconos
                modifier = Modifier.size(32.dp)
            )*/
        }
    }
}
