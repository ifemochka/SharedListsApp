package com.example.sharedlistsapp.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedlistsapp.R
import com.example.sharedlistsapp.ui.theme.Pink80

@Composable
fun DrawerHeader(
    email: String,
    id: Int = R.drawable.account_icon
) {
    Column (
        Modifier.fillMaxWidth().height(170.dp).background(Pink80),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            modifier = Modifier.size(90.dp),
            painter = painterResource(id = id),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Ваш аккуант: $email",
            fontSize = 16.sp
        )
    }
}