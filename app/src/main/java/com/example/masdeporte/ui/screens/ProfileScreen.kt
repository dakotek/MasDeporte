package com.example.masdeporte.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.masdeporte.R
import com.example.masdeporte.ui.theme.MasDeporteTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: LoginSignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showMenu by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("") }
    var userUid by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        val user = viewModel.getUserFromDatabase()
        user?.let {
            userUid = it.userId
            userName = it.name
            userEmail = it.email
            userType = it.userType
            showMenu = false
        }
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_person_pin_24),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp)
                            .padding(top = 25.dp)
                    )
                }
            }
            item {
                Row {
                    Text(
                        text = "Nombre:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " $userName",
                        fontSize = 18.sp,
                    )
                }
            }
            item {
                Row {
                    Text(
                        text = "Correo:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " $userEmail",
                        fontSize = 18.sp,
                    )
                }
            }
            item {
                Row {
                    Text(
                        text = "Tipo de usuario:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " $userType",
                        fontSize = 18.sp,
                    )
                }
            }
            item {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    color = Color.Gray,
                )
            }
            item {
                Text(
                    text = "Mis Sitios Favoritos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                Text(
                    text = "NO HAY SITIOS GUARDADOS, VE AL MAPA PARA GUARDAR TUS SITIOS FAVORITOS",
                    fontSize = 18.sp,
                )
            }
            /*
            // Lista de sitios favoritos utilizando LazyColumn
            items(/*favoriteSites*/) { site ->
                //... Reemplazar con la lógica para mostrar sitios favoritos
                // FavoriteSiteItem(site = site)
            }

             */
        }
    }
}
/*

@Composable
fun FavoriteSiteItem(site: String) {
    Text(
        text = "• $site",
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 16.dp)
    )
}
*/

@Composable
@Preview(showBackground = true)
fun FavoriteScreenPreview() {
    MasDeporteTheme {
        ProfileScreen(navController = rememberNavController())
    }
}