package com.example.masdeporte.ui.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masdeporte.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginSignUpViewModel:ViewModel() {
    private val auth:FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    private val _signInError = MutableLiveData<Boolean>()
    val signInError: LiveData<Boolean> get() = _signInError

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("MasDeporte", "signInWithEmailAndPassword logueado")
                        home()
                        _signInError.value = false
                    } else {
                        Log.d("MasDeporte", "signInWithEmailAndPassword: ${task.result.toString()}")
                        _signInError.value = true
                    }
                }
        } catch (ex:Exception) {
            Log.d("MasDeporte", "signInWithEmailAndPassword: ${ex.message}")
            _signInError.value = true
        }
    }

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

    fun createUser(name: String) {
        val userId = auth.currentUser?.uid

        val user = User (
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
}