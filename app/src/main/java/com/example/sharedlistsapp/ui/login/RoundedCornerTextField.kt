package com.example.sharedlistsapp.ui.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.example.sharedlistsapp.ui.theme.Pink80

@Composable
fun RoundedCornerTextField(text: String, label: String, onValueChange: (String) -> Unit) {
    TextField(value = text, onValueChange = {onValueChange(it)},
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().border(1.dp, Pink80, RoundedCornerShape(30.dp)),
        label = {
            Text(text = label, color = Color.LightGray)
        }
    )
}