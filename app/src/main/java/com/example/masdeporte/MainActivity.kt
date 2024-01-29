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
import com.example.masdeporte.ui.screens.AceptSitesAdminScreen
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
                //MainScreen()
                val navController = rememberNavController()

                NavHost(navController, startDestination = "main") {
                    // Pantalla Main
                    composable("main") {
                        MainScreen(navController = navController)
                    }
                    // Pantalla Login
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    // Pantalla SignUp
                    composable("signUp") {
                        SignUpScreen(navController = navController)
                    }
                    composable("map") {
                        MapScreen(navController = navController)
                    }
                    composable("favorite") {
                        FavoriteScreen(navController = navController)
                    }
                    composable("addSites") {
                        AddSitesScreen(navController = navController)
                    }
                    composable("aceptSites") {
                        AceptSitesAdminScreen(navController = navController)
                    }
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
        MapScreen(navController = rememberNavController())
    }
}