package com.example.sharedlistsapp.ui.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sharedlistsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onAuthSuccess: (uid: String, email: String) -> Unit
) {
    val auth = Firebase.auth
    val context = LocalContext.current

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(auth.currentUser) {
        auth.currentUser?.let { user ->
            onAuthSuccess(user.uid, user.email ?: "")
        }
    }

    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = "BG",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RoundedCornerTextField(
            text = emailState.value,
            label = "Email"
        ) { emailState.value = it }

        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = passwordState.value,
            label = "Password"
        ) { passwordState.value = it }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            LoginButton(text = "Sign In") {
                signIn(auth, emailState.value, passwordState.value, isLoading, onAuthSuccess, context)
            }
            LoginButton(text = "Sign Up") {
                signUp(auth, emailState.value, passwordState.value, isLoading, onAuthSuccess, context)
            }
        }
    }
}

private fun createUserDocument(uid: String, email: String) {
    val db = Firebase.firestore
    val userData = hashMapOf(
        "uid" to uid,
        "email" to email,
        "sharedLists" to emptyList<String>(),
        "createdAt" to FieldValue.serverTimestamp()
    )

    db.collection("users")
        .document(uid)
        .set(userData)
        .addOnSuccessListener {
            Log.d("MyLog", "User document created successfully for UID: $uid")
        }
        .addOnFailureListener { e ->
            Log.e("MyLog", "Error creating user document: ${e.message}")
        }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    isLoading: MutableState<Boolean>,
    onAuthSuccess: (uid: String, email: String) -> Unit,
    context: Context
) {
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
        return
    }

    isLoading.value = true
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = auth.currentUser
            user?.let {
                createUserDocument(it.uid, it.email ?: "")

                onAuthSuccess(it.uid, it.email ?: "")
                Log.d("MyLog", "Sign Up successful for user: ${it.uid}")
            }
        } else {
            Toast.makeText(context, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            Log.d("MyLog", "Sign Up failure: ${task.exception}")
        }
        isLoading.value = false
    }
}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    isLoading: MutableState<Boolean>,
    onAuthSuccess: (uid: String, email: String) -> Unit,
    context: Context
) {
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
        return
    }

    isLoading.value = true
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = auth.currentUser
            user?.let {
                onAuthSuccess(it.uid, it.email ?: "")
                Log.d("MyLog", "Sign In successful for user: ${it.uid}")
            }
        } else {
            Toast.makeText(context, "Ошибка входа: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            Log.d("MyLog", "Sign In failure: ${task.exception}")
        }
        isLoading.value = false
    }
}
