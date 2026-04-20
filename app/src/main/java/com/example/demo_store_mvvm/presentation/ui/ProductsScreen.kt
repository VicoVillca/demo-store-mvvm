package com.example.demo_store_mvvm.presentation.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.demo_store_mvvm.data.local.entity.Product
import com.example.demo_store_mvvm.presentation.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: StoreViewModel) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val excelFiles by viewModel.excelFiles.collectAsState()

    val context = LocalContext.current

    var showFilesDialog by remember { mutableStateOf(false) }

    val documentTreeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            viewModel.loadExcelFilesFromDirectory(context, it)
            showFilesDialog = true
        }
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { documentTreeLauncher.launch(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Carga Masiva Excel")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (products.isEmpty()) {
                Text(
                    text = "No hay productos registrados.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(products) { product ->
                        ProductItem(product)
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showFilesDialog && excelFiles.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showFilesDialog = false },
            title = { Text("Selecciona un Archivo Excel") },
            text = {
                LazyColumn {
                    items(excelFiles) { file ->
                        Text(
                            text = file.name ?: "Desconocido",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showFilesDialog = false
                                    viewModel.processExcelFile(context, file.uri)
                                }
                                .padding(16.dp)
                        )
                        Divider()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilesDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    } else if (showFilesDialog && excelFiles.isEmpty() && !isLoading) {
         AlertDialog(
            onDismissRequest = { showFilesDialog = false },
            title = { Text("Aviso") },
            text = { Text("No se encontraron archivos Excel (.xls, .xlsx) en esta carpeta.") },
            confirmButton = {
                TextButton(onClick = { showFilesDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ProductItem(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Código: ${product.code}")
            Text(text = "Precio: ${product.price} | Stock: ${product.stock}")
            Text(text = "Cat ID: ${product.categoryId}")
        }
    }
}
