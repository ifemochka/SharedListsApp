package com.example.sharedlistsapp.data

import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Main : Screen("main?uid={uid}&email={email}") {
        fun createRoute(uid: String, email: String): String {
            return "main?uid=$uid&email=${URLEncoder.encode(email, "UTF-8")}"
        }
    }
    data object SharedList : Screen("shared_list?uid={uid}&listName={listName}") {
        fun createRoute(uid: String, listName: String): String {
            return "shared_list?uid=$uid&listName=${URLEncoder.encode(listName, "UTF-8")}"
        }
    }
}