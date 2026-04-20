package com.example.demo_store_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Product")
data class Product(
    @PrimaryKey
    val code: String, // Assuming string code since it's from Excel, otherwise an auto-generated ID
    val name: String,
    val price: Double,
    val stock: Int,
    val categoryId: Int
)
