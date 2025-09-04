package com.example.sharedlistsapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(
    uid: String,
    email: String,
    onLogout: () -> Unit,
    onOpenList: (String) -> Unit
) {
    val listsState = remember { mutableStateOf(emptyList<String>()) }
    val loadingState = remember { mutableStateOf(true) }
    val auth = Firebase.auth
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Используем DisposableEffect для управления подпиской
    DisposableEffect(uid) {
        val db = Firebase.firestore
        val userRef = db.collection("users").document(uid)

        // Snapshot listener для реального времени
        val registration = userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Ошибка snapshot listener: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val listIds = snapshot.get("sharedLists") as? List<String> ?: emptyList()

                if (listIds.isEmpty()) {
                    listsState.value = emptyList()
                    loadingState.value = false
                    return@addSnapshotListener
                }

                // Загружаем названия списков
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val lists = db.collection("lists")
                            .whereIn(FieldPath.documentId(), listIds)
                            .get()
                            .await()

                        val listNames = lists.documents.map { document ->
                            document.getString("name") ?: "Без названия"
                        }

                        withContext(Dispatchers.Main) {
                            listsState.value = listNames
                            loadingState.value = false
                        }
                    } catch (e: Exception) {
                        println("Ошибка загрузки списков: ${e.message}")
                        withContext(Dispatchers.Main) {
                            loadingState.value = false
                        }
                    }
                }
            }
        }

        onDispose {
            registration.remove()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(modifier = Modifier.fillMaxWidth(0.7f)) {
                Column(Modifier.fillMaxSize()) {
                    DrawerHeader(email)
                    DrawerBody()
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (loadingState.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                ListsScreen(
                    auth = auth,
                    onLogout = onLogout,
                    objects = listsState.value,
                    onOpenList = onOpenList
                )
            }
        }
    }
}

private suspend fun getUserLists(userId: String): List<String> {
    val db = Firebase.firestore

    return try {
        val userDoc = db.collection("users").document(userId).get().await()
        val listIds = userDoc.get("sharedLists") as? List<String> ?: emptyList()

        if (listIds.isEmpty()) {
            return emptyList()
        }

        val lists = db.collection("lists")
            .whereIn(FieldPath.documentId(), listIds)
            .get()
            .await()

        lists.documents.map { document ->
            document.getString("name") ?: "Без названия"
        }

    } catch (e: Exception) {
        emptyList()
    }
}