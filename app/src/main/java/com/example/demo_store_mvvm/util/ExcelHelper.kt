package com.example.demo_store_mvvm.util

import android.content.Context
import android.net.Uri
import com.example.demo_store_mvvm.data.local.entity.Category
import com.example.demo_store_mvvm.data.local.entity.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

object ExcelHelper {

    suspend fun parseExcelFile(
        context: Context,
        uri: Uri
    ): Pair<List<Product>, List<Category>> = withContext(Dispatchers.IO) {
        val products = mutableListOf<Product>()
        val categories = mutableListOf<Category>()

        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val workbook = WorkbookFactory.create(inputStream)

            // Read Products from 'Datos' sheet
            val dataSheet = workbook.getSheet("Datos")
            if (dataSheet != null) {
                for (i in 1..dataSheet.lastRowNum) {
                    val row = dataSheet.getRow(i) ?: continue

                    // Assuming columns: 0=code, 1=name, 2=price, 3=stock, 4=categoryId
                    val codeCell = row.getCell(0)
                    val nameCell = row.getCell(1)
                    val priceCell = row.getCell(2)
                    val stockCell = row.getCell(3)
                    val categoryIdCell = row.getCell(4)

                    if (codeCell == null || nameCell == null) continue

                    val code = when (codeCell.cellType) {
                        org.apache.poi.ss.usermodel.CellType.NUMERIC -> codeCell.numericCellValue.toLong().toString()
                        else -> codeCell.stringCellValue ?: ""
                    }

                    val name = nameCell.stringCellValue ?: ""

                    val price = try {
                        priceCell?.numericCellValue ?: 0.0
                    } catch (e: Exception) {
                        try {
                           priceCell?.stringCellValue?.toDouble() ?: 0.0
                        } catch (e2: Exception) { 0.0 }
                    }

                    val stock = try {
                        stockCell?.numericCellValue?.toInt() ?: 0
                    } catch (e: Exception) {
                        try {
                            stockCell?.stringCellValue?.toInt() ?: 0
                        } catch (e2: Exception) { 0 }
                    }

                    val categoryId = try {
                        categoryIdCell?.numericCellValue?.toInt() ?: 0
                    } catch (e: Exception) {
                         try {
                            categoryIdCell?.stringCellValue?.toInt() ?: 0
                        } catch (e2: Exception) { 0 }
                    }

                    if (code.isNotBlank()) {
                         products.add(Product(code, name, price, stock, categoryId))
                    }
                }
            }

            // Read Categories from 'Categorias' sheet
            val catSheet = workbook.getSheet("Categorias")
            if (catSheet != null) {
                for (i in 1..catSheet.lastRowNum) {
                    val row = catSheet.getRow(i) ?: continue

                    // Assuming columns: 0=id, 1=description
                    val idCell = row.getCell(0)
                    val descCell = row.getCell(1)

                    if (idCell == null || descCell == null) continue

                    val id = try {
                        idCell.numericCellValue.toInt()
                    } catch (e: Exception) {
                         try {
                            idCell.stringCellValue.toInt()
                        } catch (e2: Exception) { continue }
                    }

                    val description = descCell.stringCellValue ?: ""

                    categories.add(Category(id, description))
                }
            }

            workbook.close()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        } finally {
            inputStream?.close()
        }

        Pair(products, categories)
    }
}
