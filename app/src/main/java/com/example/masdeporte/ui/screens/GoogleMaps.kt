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
import android.widget.Button
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
import java.util.UUID

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyGoogleMaps(context: Context, email: String, coroutineScope: CoroutineScope) {
    // Lista mutable de marcadores
    var markers by remember { mutableStateOf(emptyList<Map<String, Any>>()) }
    // Estado para la posición de la cámara
    val cameraPositionState = rememberCameraPositionState {}
    // Ubicación por defecto (Madrid)
    val currentLocation = LatLng(40.4168, -3.7038)
    // Establecer la posición inicial de la cámara
    cameraPositionState.position = CameraPosition(currentLocation, 8f, 0f, 0f)

    coroutineScope.launch {
        // Cargar los marcadores aceptados de la base de datos
        markers = loadMarkersFromDatabase().filter { it["accepted"] == true }
    }

    // Componente de mapa de Google
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        onMapClick = { latLng ->
            showDialogMakeMarker(context, latLng, email)
        },
        cameraPositionState = cameraPositionState,
    ) {
        // Recorrer y mostrar los marcadores en el mapa
        markers.forEach { markerData ->
            val position = LatLng(markerData["latitude"] as Double, markerData["longitude"] as Double)
            val title = markerData["title"] as String
            val sport = markerData["sport"] as String
            val description = markerData["description"] as String
            val rating = (markerData["rating"] as Long).toInt()
            val addedByUserEmail =  markerData["addedByUserEmail"] as String
            val markerId = markerData["markerId"] as String

            // Determinar el icono según el deporte
            val iconSport = when (sport) {
                "Fútbol" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.futbolmarker)
                "Baloncesto" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.baloncestomarker)
                "Escalada" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.escaladamarker)
                "Parkour" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.parkourmarker)
                "Skate" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.skatemarker)
                "Bici" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.bicimarker)
                "Fronton" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.frontonmarker)
                "Tenis" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.tenismarker)
                "Otro" -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.otromarker)
                else -> BitmapDescriptorFactory.fromResource(com.example.masdeporte.R.drawable.otromarker)
            }

            // Agregar marcador al mapa
            Marker(
                position = position,
                icon = iconSport,
                onClick = {
                    showMarkerDetailsDialog(context, title, sport, description, rating, addedByUserEmail, markerId, email)
                    true
                },
            )
        }
    }
}

/**
 * Muestra un diálogo para agregar un nuevo marcador en el mapa.
 * @param context El contexto de la actividad o fragmento.
 * @param latLng La posición donde se ha hecho clic en el mapa.
 * @param email El correo electrónico del usuario actual.
 */
private fun showDialogMakeMarker(context: Context, latLng: LatLng, email: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Agregar Marcador")

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL

    // Campo de entrada para el título del marcador
    val titleEditText = EditText(context)
    titleEditText.hint = "Título"
    layout.addView(titleEditText)
    addMarginBottomAndTop(titleEditText)

    // Spinner para seleccionar el deporte del marcador
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

    // Campo de entrada para la descripción del marcador
    val descriptionEditText = EditText(context)
    descriptionEditText.hint = "Descripción"
    layout.addView(descriptionEditText)
    addMarginBottom(descriptionEditText)

    // Spinner para seleccionar la valoración del marcador
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
        // Obtener los valores ingresados por el usuario
        val title = titleEditText.text.toString()
        val sport = sports[sportSpinner.selectedItemPosition]
        val description = descriptionEditText.text.toString()
        val rating = ratingArray[ratingSpinner.selectedItemPosition]

        // Agregar el marcador a la base de datos
        addMarkerToDatabase(latLng, title, sport, description, rating, email)
    }

    builder.setNegativeButton("Cancelar") { dialog, _ ->
        dialog.cancel()
    }

    builder.show()
}

/**
 * Muestra un diálogo con los detalles de un marcador en el mapa.
 * @param context El contexto de la actividad o fragmento.
 * @param title El título del marcador.
 * @param sport El deporte asociado al marcador.
 * @param description La descripción del marcador.
 * @param rating La valoración del marcador.
 * @param addedByUserEmail El correo electrónico del usuario que agregó el marcador.
 * @param markerId El ID del marcador.
 * @param userEmail El correo electrónico del usuario actual.
 */
private fun showMarkerDetailsDialog(
    context: Context,
    title: String,
    sport: String,
    description: String,
    rating: Int,
    addedByUserEmail: String,
    markerId: String,
    userEmail: String,
) {
    val builder = AlertDialog.Builder(context)

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL
    layout.gravity = Gravity.CENTER
    layout.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    )

    // TextView para mostrar el título del marcador
    val titleTextView = TextView(context)
    titleTextView.text = title
    titleTextView.gravity = Gravity.CENTER
    titleTextView.setTypeface(null, Typeface.BOLD)
    titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
    addMarginBottomAndTop(titleTextView)
    layout.addView(titleTextView)

    // TextView para mostrar el deporte del marcador
    val sportTextView = TextView(context)
    sportTextView.text = sport
    sportTextView.gravity = Gravity.CENTER
    sportTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    addMarginBottom(sportTextView)
    layout.addView(sportTextView)

    // TextView para mostrar la descripción del marcador
    val descriptionTextView = TextView(context)
    descriptionTextView.text = description
    descriptionTextView.gravity = Gravity.CENTER
    descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    addMarginBottom(descriptionTextView)
    layout.addView(descriptionTextView)

    // TextView para mostrar la valoración del marcador
    val ratingTextView = TextView(context)
    if (rating > 1) {
        ratingTextView.text = "Valorada en $rating estrellas"
    } else {
        ratingTextView.text = "Valorada en $rating estrella"
    }
    ratingTextView.gravity = Gravity.CENTER
    ratingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    layout.addView(ratingTextView)

    // Crear un LinearLayout horizontal para mostrar las estrellas de la valoración
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

    // TextView para mostrar el usuario que agregó el marcador
    val addedByUserEmailTextView = TextView(context)
    addedByUserEmailTextView.text = "Añadido por: $addedByUserEmail"
    addedByUserEmailTextView.gravity = Gravity.CENTER
    addedByUserEmailTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    addMarginBottom(addedByUserEmailTextView)
    layout.addView(addedByUserEmailTextView)

    // ImageView para agregar o eliminar de favoritos
    val favoritesButton = ImageView(context)
    var isFavorite = false

    isMarkerInFavorites(markerId, userEmail) { result ->
        isFavorite = result
        if (isFavorite) {
            favoritesButton.setImageResource(com.example.masdeporte.R.drawable.baseline_favorite_24)
        } else {
            favoritesButton.setImageResource(com.example.masdeporte.R.drawable.baseline_favorite_border_24)
        }

        layout.addView(favoritesButton)
        addMarginBottom(favoritesButton)

        // Cambiar el estado de favoritos al hacer clic
        favoritesButton.setOnClickListener {
            if (isFavorite) {
                removeMarkerFromFavorites(markerId, userEmail)
                favoritesButton.setImageResource(com.example.masdeporte.R.drawable.baseline_favorite_border_24)
                isFavorite = false
            } else {
                addMarkerToFavorites(markerId, userEmail)
                favoritesButton.setImageResource(com.example.masdeporte.R.drawable.baseline_favorite_24)
                isFavorite = true
            }
        }
    }

    // Botón para ver reseñas
    val viewReviewsButton = Button(context)
    viewReviewsButton.text = "Ver reseñas"
    viewReviewsButton.setOnClickListener {
        showReviewsDialog(context, title, markerId, userEmail)
    }
    addMarginBottom(viewReviewsButton)
    layout.addView(viewReviewsButton)

    builder.setView(layout)

    val firestore = FirebaseFirestore.getInstance()
    val favoritesCollection = firestore.collection("favorites")

    favoritesCollection
        .whereEqualTo("email", userEmail)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                val data = hashMapOf(
                    "email" to userEmail,
                    "favorites" to emptyList<String>()
                )
                favoritesCollection.add(data)
                    .addOnSuccessListener {
                        Log.d("MasDeporte", "Se creó un nuevo documento en la colección 'favorites'")
                    }
                    .addOnFailureListener { e ->
                        Log.e("MasDeporte", "Error al crear un nuevo documento en la colección 'favorites'", e)
                    }
            }
        }

    builder.setPositiveButton("Cerrar") { dialog, _ ->
        dialog.dismiss()
    }

    builder.show()
}

/**
 * Verifica si un marcador está en favoritos para un usuario dado.
 * @param markerId El ID del marcador.
 * @param userEmail El correo electrónico del usuario.
 * @param callback Una función de retorno de llamada que se invocará con el resultado.
 */
fun isMarkerInFavorites(markerId: String, userEmail: String, callback: (Boolean) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val favoritesCollection = firestore.collection("favorites")

    favoritesCollection
        .whereEqualTo("email", userEmail)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val favorites = documents.documents[0].get("favorites") as? List<String> ?: emptyList()
                val isFavorite = favorites.contains(markerId)
                callback(isFavorite)
            } else {
                callback(false)
            }
        }
        .addOnFailureListener { e ->
            Log.e("MasDeporte", "Error al verificar si el marcador está en favoritos", e)
            callback(false)
        }
}

/**
 * Agrega un marcador a la lista de favoritos de un usuario.
 * @param markerId El ID del marcador.
 * @param userEmail El correo electrónico del usuario.
 */
fun addMarkerToFavorites(markerId: String, userEmail: String) {
    val firestore = FirebaseFirestore.getInstance()
    val favoritesCollection = firestore.collection("favorites")

    favoritesCollection
        .whereEqualTo("email", userEmail)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val documentId = documents.documents[0].id
                val favorites = documents.documents[0].get("favorites") as? MutableList<String> ?: mutableListOf()
                favorites.add(markerId)
                favoritesCollection.document(documentId)
                    .update("favorites", favorites)
                    .addOnFailureListener { e ->
                        Log.e("MasDeporte", "Error al agregar el marcador a favoritos", e)
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e("MasDeporte", "Error al obtener el documento de favoritos del usuario", e)
        }
}

/**
 * Elimina un marcador de la lista de favoritos de un usuario.
 * @param markerId El ID del marcador.
 * @param userEmail El correo electrónico del usuario.
 */
fun removeMarkerFromFavorites(markerId: String, userEmail: String) {
    val firestore = FirebaseFirestore.getInstance()
    val favoritesCollection = firestore.collection("favorites")

    favoritesCollection
        .whereEqualTo("email", userEmail)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val documentId = documents.documents[0].id
                val favorites = documents.documents[0].get("favorites") as? MutableList<String> ?: mutableListOf()
                favorites.remove(markerId)
                favoritesCollection.document(documentId)
                    .update("favorites", favorites)
                    .addOnFailureListener { e ->
                        Log.e("MasDeporte", "Error al eliminar el marcador de favoritos", e)
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e("MasDeporte", "Error al obtener el documento de favoritos del usuario", e)
        }
}

/**
 * Agrega márgenes en la parte inferior para la vista dada.
 * @param view La vista a la que se agregarán los márgenes.
 */
fun addMarginBottom(view: View) {
    val layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(0, 0, 0, 50)
    view.layoutParams = layoutParams
}
/**
 * Agrega márgenes en la parte superior e inferior para la vista dada.
 * @param view La vista a la que se agregarán los márgenes.
 */
fun addMarginBottomAndTop(view: View) {
    val layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(0, 50, 0, 50)
    view.layoutParams = layoutParams
}

/**
 * Agrega un marcador a la base de datos Firestore.
 * @param latLng La posición del marcador.
 * @param title El título del marcador.
 * @param sport El deporte asociado al marcador.
 * @param description La descripción del marcador.
 * @param rating La valoración del marcador.
 * @param addedByUserEmail El correo electrónico del usuario que agregó el marcador.
 */
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
        "accepted" to false,
        "markerId" to UUID.randomUUID().toString()
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

/**
 * Carga los marcadores desde la base de datos Firestore.
 * @return Una lista de mapas que representan los marcadores.
 */
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

/**
 * Muestra un diálogo para ver y agregar reseñas a un marcador.
 * @param context El contexto de la actividad o fragmento.
 * @param title El título del marcador.
 * @param markerId El ID del marcador.
 * @param userEmail El correo electrónico del usuario actual.
 */
private fun showReviewsDialog(context: Context, title: String, markerId: String, userEmail: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Reseñas de $title")

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL

    val firestore = FirebaseFirestore.getInstance()
    val reviewsCollection = firestore.collection("reviews")

    // Obtener reseñas del marcado
    reviewsCollection
        .whereEqualTo("markerId", markerId)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // Si no hay reseñas, mostrar un mensaje indicándolo
                val noReviewsTextView = TextView(context)
                noReviewsTextView.text = "No hay reseñas todavía."
                noReviewsTextView.gravity = Gravity.CENTER
                layout.addView(noReviewsTextView)
            } else {
                // Si no hay reseñas, mostrar un mensaje indicándolo
                for (document in documents) {
                    val reviewText = document.getString("review")
                    val reviewUserEmail = document.getString("userEmail")

                    val reviewTextView = TextView(context)
                    reviewTextView.text = "($reviewUserEmail) - $reviewText"
                    addMarginBottom(reviewTextView)
                    layout.addView(reviewTextView)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("MasDeporte", "Error al obtener las reseñas", e)
        }

    // EditText para agregar una nueva reseña
    val newReviewEditText = EditText(context)
    newReviewEditText.hint = "Escribe tu reseña aquí"
    layout.addView(newReviewEditText)
    addMarginBottomAndTop(newReviewEditText)

    builder.setView(layout)

    builder.setPositiveButton("Agregar reseña") { _, _ ->
        val markerId = markerId
        val review = newReviewEditText.text.toString()
        val userEmail = userEmail

        addReviewToDatabase(markerId, review, userEmail)
    }

    builder.setNegativeButton("Cancelar") { dialog, _ ->
        dialog.cancel()
    }

    builder.show()
}

/**
 * Agrega una reseña a la base de datos Firestore.
 * @param markerId El ID del marcador al que se agregará la reseña.
 * @param reviewText El texto de la reseña.
 * @param userEmail El correo electrónico del usuario que agrega la reseña.
 */
private fun addReviewToDatabase(markerId: String, reviewText: String, userEmail: String) {
    val firestore = FirebaseFirestore.getInstance()
    val reviewsCollection = firestore.collection("reviews")

    val reviewData = hashMapOf(
        "markerId" to markerId,
        "review" to reviewText,
        "userEmail" to userEmail
    )

    reviewsCollection
        .add(reviewData)
        .addOnSuccessListener { documentReference ->
            Log.d("MasDeporte", "Reseña agregada con ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.e("MasDeporte", "Error al agregar reseña", e)
        }
}