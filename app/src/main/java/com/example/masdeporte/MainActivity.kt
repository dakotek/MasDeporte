package com.example.masdeporte

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.masdeporte.room.AppDatabase
import com.example.masdeporte.ui.screens.AboutAppScreen
import com.example.masdeporte.ui.screens.AcceptSitesAdminScreen
import com.example.masdeporte.ui.screens.LoginScreen
import com.example.masdeporte.ui.screens.MainScreen
import com.example.masdeporte.ui.screens.MapScreen
import com.example.masdeporte.ui.screens.ProfileScreen
import com.example.masdeporte.ui.screens.SignUpScreen
import com.example.masdeporte.ui.theme.MasDeporteTheme

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            // Inicialización de la base de datos Room
            database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "dataUserSession"
            ).build()

            setContent {
                MasDeporteTheme {
                    // Controlador de navegación
                    val navController = rememberNavController()

                    // Definición de las pantallas y sus rutas en el NavHost
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
                        composable("profile") {
                            ProfileScreen(navController = navController)
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
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MainActividad", "Error en onCreate: ${e.message}")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainScreenPreview() {
    MasDeporteTheme {

    }
}