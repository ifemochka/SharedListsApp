package com.example.sharedlistsapp.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedlistsapp.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DrawerBody(auth: FirebaseAuth,
               onLogout: () -> Unit) {
    val options = listOf(
        "Ваши списки"
    )

    Box(modifier = Modifier.fillMaxSize()){
        Image(modifier = Modifier.fillMaxSize(), painter = painterResource(R.drawable.background),
            contentDescription = "", contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
        {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(options){ item ->
                    Column (Modifier.fillMaxWidth().clickable(){}) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = item,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().height(1.dp)
                                .background(Color.LightGray)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    auth.signOut()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Выйти из аккаунта")
            }

        }
    }
}