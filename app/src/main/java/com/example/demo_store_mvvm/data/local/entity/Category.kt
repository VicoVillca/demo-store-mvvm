package com.example.demo_store_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class Category(
    @PrimaryKey
    val id: Int,
    val description: String
)
