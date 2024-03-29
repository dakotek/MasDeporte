package com.example.masdeporte.ui.screens

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masdeporte.MainActivity
import com.example.masdeporte.models.User
import com.example.masdeporte.room.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginSignUpViewModel : ViewModel() {
    // Instancias de FirebaseAuth y MutableLiveData
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    private val _signInError = MutableLiveData<Boolean>()

    // Instancia del DAO de la base de datos local
    private val userDao = MainActivity.database.userDao()

    // Función para obtener un usuario de la base de datos local
    suspend fun getUserFromDatabase(): UserEntity? {
        return userDao.getUser()
    }

    // Función para iniciar sesión con correo electrónico y contraseña
    fun signInWithEmailAndPassword(email: String, password: String, onSignInComplete: (String?) -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Obtener el UID del usuario actualmente autenticado
                            val uid = auth.currentUser?.uid
                            Log.d("MasDeporte", "signInWithEmailAndPassword logueado")
                            onSignInComplete(uid)

                            // Obtener y establecer los detalles del usuario
                            if (uid != null) {
                                checkUserDetailsByUid(uid) { userDetails ->
                                    if (userDetails != null) {
                                        val nameUser = userDetails.name.toString()
                                        val typeUser = userDetails.userType.toString()
                                        viewModelScope.launch {
                                            userDao.deleteAllUsers()
                                            val user = UserEntity(uid, nameUser, email, typeUser)
                                            userDao.insertUser(user)
                                            Log.d("Detalles del usuario", "${getUserFromDatabase()}")
                                        }
                                        Log.d("Detalles del usuario", "$nameUser, $email, $typeUser, $uid")
                                    }
                                }
                            }

                            _signInError.value = false
                        } else {
                            Log.d("MasDeporte", "signInWithEmailAndPassword: ${task.result.toString()}")
                            onSignInComplete(null)
                            _signInError.value = true
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MasDeporte", "signInWithEmailAndPassword: ${ex.message}")
                onSignInComplete(null)
                _signInError.value = true
            }
        }

    // Función para crear un usuario con correo electrónico y contraseña
    fun createUserWithEmailAndPassword(name: String, email: String, password: String, home: () -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        createUser(name)
                        home()
                    } else {
                        Log.d("MasDeporte", "createUserWithEmailAndPassword: ${task.result.toString()}")
                    }
                    _loading.value = false
                }
        }
    }

    // Función para crear un usuario en Firestore
    fun createUser(name: String) {
        val userId = auth.currentUser?.uid

        val user = User(
            userId = userId.toString(),
            name = name,
            userType = "STANDARD",
            id = null
        ).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
            .addOnSuccessListener {
                Log.d("MasDeporte", "Usuario creado: ${it.id}")
            }.addOnFailureListener {
                Log.d("MasDeporte", "Error usuario creado: ${it}")
            }
    }

    // Modelo de datos para los detalles del usuario
    data class UserDetails(val userType: String?, val name: String?)

    // Función para verificar los detalles del usuario por UID en Firestore
    fun checkUserDetailsByUid(uid: String, onUserDetailsReceived: (UserDetails?) -> Unit) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("user_id", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userType = querySnapshot.documents[0].getString("userType")
                    val name = querySnapshot.documents[0].getString("name")
                    val userDetails = UserDetails(userType, name)
                    onUserDetailsReceived(userDetails)
                } else {
                    onUserDetailsReceived(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("MasDeporte", "Error al obtener detalles del usuario por UID: $exception")
                onUserDetailsReceived(null)
            }
    }
}