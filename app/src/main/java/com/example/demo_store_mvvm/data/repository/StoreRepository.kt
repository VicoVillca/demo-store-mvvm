package com.example.demo_store_mvvm.data.repository

import android.content.Context
import android.net.Uri
import com.example.demo_store_mvvm.data.local.dao.StoreDao
import com.example.demo_store_mvvm.util.ExcelHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StoreRepository(private val storeDao: StoreDao) {

    val allProducts = storeDao.getAllProducts()
    val allCategories = storeDao.getAllCategories()

    /**
     * Parses the Excel file and inserts into the database.
     */
    suspend fun processExcelAndSave(context: Context, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val (products, categories) = ExcelHelper.parseExcelFile(context, uri)
            
            // Insert parsed data to local DB
            if (categories.isNotEmpty()) {
                storeDao.insertCategories(categories)
            }
            if (products.isNotEmpty()) {
                storeDao.insertProducts(products)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
