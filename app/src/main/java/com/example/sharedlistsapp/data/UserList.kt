package com.example.sharedlistsapp.data

import java.util.Date

data class UserList(
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val sharedWith: List<String> = emptyList(),
    val items: List<String> = emptyList(),
    val createdAt: Date = Date()
)