package com.example.sharedlistsapp.ui.sharedlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.sharedlistsapp.R
import com.example.sharedlistsapp.data.ShoppingItem
import com.example.sharedlistsapp.data.UserList
import com.example.sharedlistsapp.ui.theme.Purple80
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

@Composable
fun SharedListScreen(
    uid: String,
    listName: String,
    listId: String
) {
    var isLoading by remember { mutableStateOf(true) }
    var userList by remember { mutableStateOf<UserList?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(listId) {
        loadUserList(listId) { loadedList ->
            userList = loadedList
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (userList != null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            SharedListContent(
                uid = uid,
                userList = userList!!,
                onListUpdate = { updatedList ->
                    updateUserList(updatedList)
                    userList = updatedList
                },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Добавить соавтора")
            }
        }

        if (showAddDialog) {
            var email by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Добавить соавтора") },
                text = {
                    Column {
                        Text("Введите email:")
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("email@gmail.com") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (email.isNotBlank()) {
                                addCollaborator(listId, email)
                                showAddDialog = false
                            }
                        },
                        enabled = email.isNotBlank()
                    ) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddDialog = false }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Список не найден")
        }
    }
}


fun loadUserList(listId: String, onLoaded: (UserList) -> Unit) {
    val db = Firebase.firestore

    db.collection("lists").document(listId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val itemsData = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                val shoppingItems = itemsData.map { itemMap ->
                    ShoppingItem(
                        name = itemMap["name"] as? String ?: "",
                        checked = itemMap["checked"] as? Boolean ?: false
                    )
                }

                val userList = UserList(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    ownerId = document.getString("ownerId") ?: "",
                    sharedWith = document.get("sharedWith") as? List<String> ?: emptyList(),
                    items = shoppingItems,
                    createdAt = document.getDate("createdAt") ?: Date()
                )

                onLoaded(userList)
            } else {
                val newUserList = UserList(
                    id = listId,
                    name = "Новый список",
                    items = listOf(ShoppingItem("", false))
                )
                onLoaded(newUserList)
            }
        }
        .addOnFailureListener { e ->
            println("Ошибка загрузки списка: ${e.message}")
            val newUserList = UserList(
                id = listId,
                name = "Новый список",
                items = listOf(ShoppingItem("", false))
            )
            onLoaded(newUserList)
        }
}

fun updateUserList(userList: UserList) {
    val db = Firebase.firestore

    val itemsForFirebase = userList.items.map { item ->
        hashMapOf(
            "name" to item.name,
            "checked" to item.checked
        )
    }

    val listData = hashMapOf(
        "id" to userList.id,
        "name" to userList.name,
        "ownerId" to userList.ownerId,
        "sharedWith" to userList.sharedWith,
        "items" to itemsForFirebase,
        "updatedAt" to FieldValue.serverTimestamp()
    )

    db.collection("lists").document(userList.id)
        .set(listData, SetOptions.merge())
        .addOnFailureListener { e ->
            println("Ошибка сохранения списка: ${e.message}")
        }
}

fun addCollaborator(listId: String, collaboratorEmail: String) {
    val db = Firebase.firestore

    db.collection("users")
        .whereEqualTo("email", collaboratorEmail)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.documents.isNotEmpty()) {
                val collaboratorDoc = querySnapshot.documents[0]
                val collaboratorId = collaboratorDoc.id

                addUserToSharedWith(listId, collaboratorId)

                addListToUser(collaboratorId, listId)

                println("Соавтор $collaboratorEmail успешно добавлен")
            } else {
                println("Пользователь с email $collaboratorEmail не найден")
            }
        }
        .addOnFailureListener { e ->
            println("Ошибка при поиске пользователя: ${e.message}")
        }
}

private fun addUserToSharedWith(listId: String, userId: String) {
    val db = Firebase.firestore
    val listRef = db.collection("lists").document(listId)

    listRef.update("sharedWith", FieldValue.arrayUnion(userId))
        .addOnSuccessListener {
            println("Пользователь добавлен в sharedWith списка $listId")
        }
        .addOnFailureListener { e ->
            println("Ошибка при добавлении в sharedWith: ${e.message}")
        }
}

private fun addListToUser(userId: String, listId: String) {
    val db = Firebase.firestore
    val userRef = db.collection("users").document(userId)

    userRef.update("sharedLists", FieldValue.arrayUnion(listId))
        .addOnSuccessListener {
            println("Список $listId добавлен пользователю $userId")
        }
        .addOnFailureListener { e ->
            println("Ошибка при добавлении списка пользователю: ${e.message}")
        }
}

