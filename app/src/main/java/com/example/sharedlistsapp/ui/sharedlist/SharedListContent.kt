package com.example.sharedlistsapp.ui.sharedlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sharedlistsapp.data.ShoppingItem
import com.example.sharedlistsapp.data.UserList
import com.example.sharedlistsapp.ui.theme.Purple80

@Composable
fun SharedListContent(
    uid: String,
    userList: UserList,
    onListUpdate: (UserList) -> Unit,
    modifier: Modifier = Modifier
) {
    var focusedIndex by remember { mutableStateOf(-1) }

    Box(
        modifier = modifier
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Список: ${userList.name}",
                style = MaterialTheme.typography.headlineMedium,
                color = Purple80,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "UID пользователя: $uid",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            userList.items.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = item.checked,
                        onCheckedChange = { isChecked ->
                            val updatedItems = userList.items.toMutableList().apply {
                                this[index] = this[index].copy(checked = isChecked)
                            }
                            onListUpdate(userList.copy(items = updatedItems))
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    BasicTextField(
                        value = item.name,
                        onValueChange = { newValue ->
                            val updatedItems = userList.items.toMutableList().apply {
                                this[index] = this[index].copy(name = newValue)

                                if (index == userList.items.size - 1 && newValue.isNotEmpty() && focusedIndex == index) {
                                    add(ShoppingItem("", false))
                                }
                            }
                            onListUpdate(userList.copy(items = updatedItems))
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    focusedIndex = index
                                } else if (focusedIndex == index) {
                                    focusedIndex = -1
                                }

                                if (!focusState.isFocused && item.name.isEmpty() && index != userList.items.size - 1) {
                                    val updatedItems = userList.items.toMutableList().apply {
                                        removeAt(index)
                                    }
                                    onListUpdate(userList.copy(items = updatedItems))
                                }
                            },
                        decorationBox = { innerTextField ->
                            if (item.name.isEmpty()) {
                                Text(
                                    text = "Добавить элемент...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.LightGray
                                )
                            }
                            innerTextField()
                        },
                        singleLine = true
                    )
                }

                if (index < userList.items.size - 1) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}
