package com.example.masdeporte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.masdeporte.ui.screens.AboutAppScreen
import com.example.masdeporte.ui.screens.AcceptSitesAdminScreen
import com.example.masdeporte.ui.screens.AddSitesScreen
import com.example.masdeporte.ui.screens.FavoriteScreen
import com.example.masdeporte.ui.screens.LoginScreen
import com.example.masdeporte.ui.screens.MainScreen
import com.example.masdeporte.ui.screens.MapScreen
import com.example.masdeporte.ui.screens.SignUpScreen
import com.example.masdeporte.ui.theme.MasDeporteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MasDeporteTheme {
                val navController = rememberNavController()

                NavHost(navController, startDestination = "main") {
                    // Pantalla "Main"
                    composable("main") {
                        MainScreen(navController = navController)
                    }
                    // Pantalla "Inicio de sesión"
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    // Pantalla "Registro"
                    composable("signUp") {
                        SignUpScreen(navController = navController)
                    }
                    // Pantalla "Mapa"
                    composable("map") {
                        MapScreen(navController = navController)
                    }
                    // Pantalla "Mis favoritos"
                    composable("favorite") {
                        FavoriteScreen(navController = navController)
                    }
                    // Pantalla "Añadir sitio"
                    composable("addSites") {
                        AddSitesScreen(navController = navController)
                    }
                    // Pantalla "Confirmar sitios (ADMIN)"
                    composable("aceptSites") {
                        AcceptSitesAdminScreen(navController = navController)
                    }
                    // Pantalla "Más sobre la app..."
                    composable("aboutApp") {
                        AboutAppScreen(navController = navController)
                    }

                }

            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainScreenPreview() {
    MasDeporteTheme {
        //MainScreen()
        //ButtonsScreen()
        //LoginScreen(navController = rememberNavController())
        //SignUpScreen()
        //MapScreen(navController = rememberNavController())
    }
}