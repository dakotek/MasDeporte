package com.example.masdeporte.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
fun AboutAppScreen(
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
                            text = { Text("Mis favoritos") },
                            onClick = { navController.navigate("favorite") },
                            leadingIcon = {
                                Icon(ImageVector.vectorResource(id = R.drawable.baseline_favorite_border_24),
                                    contentDescription = null)
                            },
                            contentPadding = PaddingValues(8.dp),
                        )
                        DropdownMenuItem(
                            text = { Text("Añadir Sitio") },
                            onClick = { navController.navigate("addSites") },
                            leadingIcon = {
                                Icon(ImageVector.vectorResource(id = R.drawable.baseline_add_location_alt_24),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Más sobre la app...",
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AboutAppScreenPreview() {
    MasDeporteTheme {
        AboutAppScreen(navController = rememberNavController())
    }
}