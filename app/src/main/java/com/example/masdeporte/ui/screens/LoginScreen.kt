package com.example.masdeporte.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.masdeporte.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginSignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    fun launchSnackbar(message: String, actionLabel: String?=null,duration: SnackbarDuration = SnackbarDuration.Short) {
        scope.launch {
            snackState.showSnackbar(message = message,actionLabel=actionLabel, duration=duration)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.login2),
            fontSize = 35.sp,
            fontWeight = FontWeight.ExtraBold,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        var content = Color.White
        // Botón para iniciar sesión
        Box(contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        content = Color.Red
                        launchSnackbar(
                            message = "Debes de rellenar todos los campos",
                            actionLabel = "Aceptar",
                            duration = SnackbarDuration.Long
                        )
                    } else {
                        launchSnackbar(
                            message = "Iniciando sesión...",
                            actionLabel = "Aceptar",
                            duration = SnackbarDuration.Long
                        )
                        viewModel.signInWithEmailAndPassword(email, password) {
                            if (viewModel.signInError.value == false) {
                                navController.navigate("map")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.login1),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize(), Alignment.BottomCenter){
            SnackbarHost(hostState = snackState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = Color.Black,
                    contentColor = content
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.signUpText),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.signUp1),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                navController.navigate("signUp")
            }
        )
    }
}
