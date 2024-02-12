package com.example.masdeporte.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.masdeporte.R
import com.example.masdeporte.ui.theme.MasDeporteTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Pantalla de "Aceptar sitios (ADMIN)" para que los administradores confirmen o rechacen los sitios propuestos por los usuarios.
 * @param navController Controlador de navegación.
 * @param viewModel ViewModel para el inicio de sesión y registro.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptSitesAdminScreen(
    navController: NavController,
    viewModel: LoginSignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Estado para controlar la visibilidad del menú desplegable
    var showMenu by remember { mutableStateOf(false) }
    // Variables para almacenar la información del usuario
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("") }
    var userUid by remember { mutableStateOf("") }

    // Lista de sitios para aceptar
    var sitesToAccept by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    // Conjunto de IDs de sitios visibles
    var visibleSiteIds by remember { mutableStateOf(emptySet<String>()) }

    // Obtener la información del usuario al cargar la pantalla
    LaunchedEffect(viewModel) {
        val user = viewModel.getUserFromDatabase()
        user?.let {
            userUid = it.userId
            userName = it.name
            userEmail = it.email
            userType = it.userType
            showMenu = false
        }
        sitesToAccept = loadSitesToAccept()
    }

    // Diseño de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.ExtraBold,
                ) },

                navigationIcon = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    }
                    // Menú desplegable con opciones de navegación
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Mapa") },
                            onClick = { navController.navigate("map") },
                            leadingIcon = {
                                Icon(ImageVector.vectorResource(id = R.drawable.baseline_map_24),
                                    contentDescription = null)
                            },
                            contentPadding = PaddingValues(8.dp),
                        )
                        DropdownMenuItem(
                            text = { Text("Mi perfil") },
                            onClick = { navController.navigate("profile") },
                            leadingIcon = {
                                Icon(ImageVector.vectorResource(id = R.drawable.baseline_person_24),
                                    contentDescription = null)
                            },
                            contentPadding = PaddingValues(8.dp),
                        )
                        // Mostrar opción adicional para usuarios de tipo "ADMIN"
                        if (userType == "ADMIN") {
                            DropdownMenuItem(
                                text = { Text("Confirmar sitios (ADMIN)") },
                                onClick = { navController.navigate("aceptSites") },
                                leadingIcon = {
                                    Icon(
                                        ImageVector.vectorResource(id = R.drawable.baseline_admin_panel_settings_24),
                                        contentDescription = null
                                    )
                                },
                                contentPadding = PaddingValues(8.dp),
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Más sobre la app…") },
                            onClick = { navController.navigate("aboutApp") },
                            leadingIcon = {
                                Icon(ImageVector.vectorResource(id = R.drawable.baseline_read_more_24),
                                    contentDescription = null)
                            },
                            contentPadding = PaddingValues(8.dp),
                        )
                    }
                },
            )
        }
    ) {
        // Lista de sitios para aceptar
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Confirmar sitios",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 45.dp)
                )
            }
            if (sitesToAccept.isNullOrEmpty() || (sitesToAccept?.size == visibleSiteIds.size)) {
                // Mostrar mensaje si no hay sitios para revisar
                item {
                    Text(
                        text = "NO HAY SITIOS PARA REVISAR Y ACEPTAR. CUANDO ALGUIEN AÑADA MÁS SITIOS SE MOSTRARÁN AQUI",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                sitesToAccept?.forEach { siteDetails ->
                    val title = siteDetails["title"] as String
                    val sport = siteDetails["sport"] as String
                    val description = siteDetails["description"] as String
                    val rating = siteDetails["rating"] as Long
                    val latitude = siteDetails["latitude"] as Double
                    val longitude = siteDetails["longitude"] as Double
                    val addedByUserEmail = siteDetails["addedByUserEmail"] as String
                    val markerId = siteDetails["markerId"] as String

                    val isVisible = !visibleSiteIds.contains(markerId)

                    if (isVisible) {
                        // Mostrar cada sitio en una tarjeta
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = sport,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = description,
                                        fontSize = 14.sp,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    if (rating > 1) {
                                        Text(
                                            text = "Valorada en $rating estrellas",
                                            fontSize = 14.sp,
                                        )
                                    } else {
                                        Text(
                                            text = "Valorada en $rating estrella",
                                            fontSize = 14.sp,
                                        )
                                    }
                                    Row {
                                        repeat(rating.toInt()) {
                                            Image(
                                                painter = painterResource(id = R.drawable.baseline_star_24),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Ubicado en: $latitude, $longitude",
                                        fontSize = 14.sp,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Añadido por: $addedByUserEmail",
                                        fontSize = 14.sp,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = {
                                                acceptSite(markerId)
                                                visibleSiteIds = visibleSiteIds + markerId
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Green,
                                                contentColor = Color.Black)
                                        ) {
                                            Text("Aceptar")
                                        }
                                        Spacer(modifier = Modifier.width(30.dp))
                                        Button(
                                            onClick = {
                                                denySite(markerId)
                                                visibleSiteIds = visibleSiteIds + markerId
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red,
                                                contentColor = Color.Black)
                                        ) {
                                            Text("Denegar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Función suspendida para cargar los sitios que necesitan ser aceptados por los administradores.
 */
suspend fun loadSitesToAccept(): List<Map<String, Any>> = withContext(Dispatchers.IO)  {
    val firestore = FirebaseFirestore.getInstance()
    val markers = mutableListOf<Map<String, Any>>()

    try {
        val result = firestore.collection("markers")
            .whereEqualTo("accepted", false)
            .get()
            .await()

        for (document in result) {
            markers.add(document.data)
        }
    } catch (exception: Exception) {
        Log.e("MasDeporte", "Error al obtener los marcadores", exception)
    }
    markers
}

/**
 * Función para aceptar un sitio propuesto por un usuario.
 * @param markerId ID del marcador a aceptar.
 */
private fun acceptSite(markerId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val markersCollection = firestore.collection("markers")

    markersCollection
        .whereEqualTo("markerId", markerId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val documentId = documents.documents[0].id
                markersCollection.document(documentId)
                    .update("accepted", true)
                    .addOnSuccessListener {
                        Log.d("MasDeporte", "Marcador $markerId aceptado exitosamente.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("MasDeporte", "Error al aceptar el marcador $markerId", e)
                    }
            } else {
                Log.e("MasDeporte", "No se encontró el marcador con ID: $markerId")
            }
        }
        .addOnFailureListener { e ->
            Log.e("MasDeporte", "Error al obtener el documento de marcadores", e)
        }
}

/**
 * Función para denegar un sitio propuesto por un usuario.
 * @param markerId ID del marcador a denegar.
 */
private fun denySite(markerId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val markersCollection = firestore.collection("markers")

    markersCollection
        .whereEqualTo("markerId", markerId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val documentId = documents.documents[0].id
                markersCollection.document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("MasDeporte", "Marcador $markerId eliminado exitosamente.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("MasDeporte", "Error al eliminar el marcador $markerId", e)
                    }
            } else {
                Log.e("MasDeporte", "No se encontró el marcador con ID: $markerId")
            }
        }
        .addOnFailureListener { e ->
            Log.e("MasDeporte", "Error al obtener el documento de marcadores", e)
        }
}

@Composable
@Preview(showBackground = true)
fun AcceptSitesAdminScreenPreview() {
    MasDeporteTheme {
        AboutAppScreen(navController = rememberNavController())
    }
}