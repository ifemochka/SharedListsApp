package com.example.sharedlistsapp.ui.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sharedlistsapp.ui.theme.Purple80

@Composable
fun LoginButton(text: String, onClick: () -> Unit) {
    Button(onClick = {
        onClick()
    },
        modifier = Modifier.fillMaxWidth(0.5f),
        colors = ButtonDefaults.buttonColors(
            containerColor = Purple80
        )) {
        Text(text = text)
    }
}