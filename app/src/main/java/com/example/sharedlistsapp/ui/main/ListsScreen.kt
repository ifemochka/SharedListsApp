package com.example.sharedlistsapp.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedlistsapp.R
import com.example.sharedlistsapp.ui.theme.Purple80
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ListsScreen(
    auth: FirebaseAuth,
    onLogout: () -> Unit,
    objects: List<String>,
    onOpenList: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = "BG",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ваши списки",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.1f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            color = Purple80,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple80)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить список",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Добавить список")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(objects) { item ->
                ObjectCard(
                    item = item,
                    onClick = { onOpenList(item) }
                )
            }
        }

        Button(
            onClick = {
                auth.signOut()
                onLogout()
            }
        ) {
            Text(text = "Выйти")
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Создать новый список") },
            text = {
                Column {
                    Text("Введите название списка:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newListName,
                        onValueChange = { newListName = it },
                        placeholder = { Text("Название списка") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            createNewList(auth.currentUser?.uid ?: "", newListName)
                            showCreateDialog = false
                            newListName = ""
                        }
                    },
                    enabled = newListName.isNotBlank()
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCreateDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

fun createNewList(userId: String, listName: String) {
    val db = Firebase.firestore

    val listData = hashMapOf(
        "name" to listName,
        "ownerId" to userId,
        "sharedWith" to emptyList<String>(),
        "createdAt" to FieldValue.serverTimestamp()
    )

    db.collection("lists")
        .add(listData)
        .addOnSuccessListener { listRef ->
            addListToUser(userId, listRef.id)
        }
        .addOnFailureListener { e ->
            println("Ошибка при создании списка: ${e.message}")
        }
}

private fun addListToUser(userId: String, listId: String) {
    val db = Firebase.firestore
    val userRef = db.collection("users").document(userId)

    db.runTransaction { transaction ->
        val user = transaction.get(userRef)
        val currentLists = user.get("sharedLists") as? List<String> ?: emptyList()
        transaction.update(userRef, "sharedLists", currentLists + listId)
    }.addOnSuccessListener {
        println("Список успешно добавлен пользователю")
    }.addOnFailureListener { e ->
        println("Ошибка при добавлении списка пользователю: ${e.message}")
    }
}