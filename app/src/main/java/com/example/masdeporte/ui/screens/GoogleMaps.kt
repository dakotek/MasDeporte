package com.example.masdeporte.ui.screens

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyGoogleMaps(context: Context, email: String, coroutineScope: CoroutineScope) {
    var markers by remember { mutableStateOf(emptyList<Map<String, Any>>()) }
    val cameraPositionState = rememberCameraPositionState {}
    val currentLocation = LatLng(40.4168, -3.7038)
    cameraPositionState.position = CameraPosition(currentLocation, 5f, 0f, 0f)

    coroutineScope.launch {
        markers = loadMarkersFromDatabase().filter { it["accepted"] == true }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        onMapClick = { latLng ->
            showDialogMakeMarker(context, latLng, email)
        },
        cameraPositionState = cameraPositionState,
    ) {
        markers.forEach { markerData ->
            val position = LatLng(markerData["latitude"] as Double, markerData["longitude"] as Double)
            val title = markerData["title"] as String
            val sport = markerData["sport"] as String
            val description = markerData["description"] as String
            val rating = (markerData["rating"] as Long).toInt()
            val addedByUserEmail =  markerData["addedByUserEmail"] as String

            Marker(
                position = position,
                onClick = {
                    showMarkerDetailsDialog(context, title, sport, description, rating, addedByUserEmail)
                    true
                }
            )
        }
    }
}

private fun showDialogMakeMarker(context: Context, latLng: LatLng, email: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Agregar Marcador")

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL

    val titleEditText = EditText(context)
    titleEditText.hint = "Título"
    layout.addView(titleEditText)
    addMarginBottomAndTop(titleEditText)

    val chooseSportText = TextView(context)
    chooseSportText.text = "Elegir Deporte"
    layout.addView(chooseSportText)
    val sportSpinner = Spinner(context)
    val sports = arrayOf("Fútbol", "Baloncesto", "Escalada", "Parkour", "Skate", "Bici", "Fronton","Tenis", "Otro")
    val sportAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, sports)
    sportAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
    sportSpinner.adapter = sportAdapter
    layout.addView(sportSpinner)
    addMarginBottom(sportSpinner)

    val descriptionEditText = EditText(context)
    descriptionEditText.hint = "Descripción"
    layout.addView(descriptionEditText)
    addMarginBottom(descriptionEditText)

    val chooseRating = TextView(context)
    chooseRating.text = "Valoración"
    layout.addView(chooseRating)
    val ratingSpinner = Spinner(context)
    val ratingArray = arrayOf(1, 2, 3, 4, 5)
    val ratingAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, ratingArray)
    ratingAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
    ratingSpinner.adapter = ratingAdapter
    layout.addView(ratingSpinner)
    addMarginBottom(ratingSpinner)

    builder.setView(layout)

    builder.setPositiveButton("Guardar") { _, _ ->
        val title = titleEditText.text.toString()
        val sport = sports[sportSpinner.selectedItemPosition]
        val description = descriptionEditText.text.toString()
        val rating = ratingArray[ratingSpinner.selectedItemPosition]

        addMarkerToDatabase(latLng, title, sport, description, rating, email)
    }

    builder.setNegativeButton("Cancelar") { dialog, _ ->
        dialog.cancel()
    }

    builder.show()
}

private fun showMarkerDetailsDialog(
    context: Context,
    title: String,
    sport: String,
    description: String,
    rating: Int,
    addedByUserEmail: String
) {
    val builder = AlertDialog.Builder(context)

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL
    layout.gravity = Gravity.CENTER
    layout.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    )

    val titleTextView = TextView(context)
    titleTextView.text = title
    titleTextView.gravity = Gravity.CENTER
    titleTextView.setTypeface(null, Typeface.BOLD)
    titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
    addMarginBottomAndTop(titleTextView)
    layout.addView(titleTextView)

    val sportTextView = TextView(context)
    sportTextView.text = sport
    sportTextView.gravity = Gravity.CENTER
    sportTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    addMarginBottom(sportTextView)
    layout.addView(sportTextView)

    val descriptionTextView = TextView(context)
    descriptionTextView.text = description
    descriptionTextView.gravity = Gravity.CENTER
    descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    addMarginBottom(descriptionTextView)
    layout.addView(descriptionTextView)

    val ratingTextView = TextView(context)
    if (rating > 1) {
        ratingTextView.text = "Valorada en: $rating estrellas"
    } else {
        ratingTextView.text = "Valorada en: $rating estrella"
    }
    ratingTextView.gravity = Gravity.CENTER
    ratingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    layout.addView(ratingTextView)

    // Crear un LinearLayout horizontal para las estrellas
    val starsLayout = LinearLayout(context)
    starsLayout.orientation = LinearLayout.HORIZONTAL
    starsLayout.gravity = Gravity.CENTER
    addMarginBottom(starsLayout)

    // Obtener la imagen de estrella del recurso
    val starDrawable = ContextCompat.getDrawable(context, com.example.masdeporte.R.drawable.baseline_star_24)

    // Mostrar imágenes de estrellas según la calificación
    for (i in 0 until rating) {
        val starImageView = ImageView(context)
        starImageView.setImageDrawable(starDrawable)
        starImageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        starsLayout.addView(starImageView)
    }
    layout.addView(starsLayout)

    val addedByUserEmailTextView = TextView(context)
    addedByUserEmailTextView.text = "Añadido por: $addedByUserEmail"
    addedByUserEmailTextView.gravity = Gravity.CENTER
    addedByUserEmailTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    addMarginBottom(addedByUserEmailTextView)
    layout.addView(addedByUserEmailTextView)

    builder.setView(layout)

    builder.setPositiveButton("Cerrar") { dialog, _ ->
        dialog.dismiss()
    }

    builder.show()
}


private fun addMarginBottom(view: View) {
    val layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(0, 0, 0, 50)
    view.layoutParams = layoutParams
}
private fun addMarginBottomAndTop(view: View) {
    val layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(0, 50, 0, 50)
    view.layoutParams = layoutParams
}

private fun addMarkerToDatabase(latLng: LatLng, title: String, sport: String, description: String, rating: Int, addedByUserEmail: String) {
    val firestore = FirebaseFirestore.getInstance()

    val markerData = hashMapOf(
        "title" to title,
        "sport" to sport,
        "description" to description,
        "rating" to rating,
        "latitude" to latLng.latitude,
        "longitude" to latLng.longitude,
        "addedByUserEmail" to addedByUserEmail,
        "accepted" to false
    )

    firestore.collection("markers")
        .add(markerData)
        .addOnSuccessListener { documentReference ->
            Log.d("MasDeporte", "Marcador agregado con ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("MasDeporte", "Error al agregar marcador", e)
        }
}

suspend fun loadMarkersFromDatabase(): List<Map<String, Any>> = withContext(Dispatchers.IO) {
    val firestore = FirebaseFirestore.getInstance()
    val markers = mutableListOf<Map<String, Any>>()

    try {
        val result = firestore.collection("markers").get().await()

        for (document in result) {
            markers.add(document.data)
        }
    } catch (exception: Exception) {
        Log.e("MasDeporte", "Error al obtener los marcadores", exception)
    }

    markers
}
