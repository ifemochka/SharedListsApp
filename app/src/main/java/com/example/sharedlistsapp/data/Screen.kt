package com.example.sharedlistsapp.data

import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main/{uid}/{email}") {
        fun createRoute(uid: String, email: String) = "main/$uid/$email"
    }

    object SharedList : Screen("shared_list/{uid}/{listId}/{listName}") {
        fun createRoute(uid: String, listId: String, listName: String) =
            "shared_list/$uid/$listId/$listName"
    }
}