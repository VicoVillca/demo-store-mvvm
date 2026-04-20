package com.example.demo_store_mvvm.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.demo_store_mvvm.data.local.entity.Product
import com.example.demo_store_mvvm.data.repository.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreViewModel(private val repository: StoreRepository) : ViewModel() {

    val products: StateFlow<List<Product>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _excelFiles = MutableStateFlow<List<DocumentFile>>(emptyList())
    val excelFiles = _excelFiles.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun loadExcelFilesFromDirectory(context: Context, treeUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val dir = DocumentFile.fromTreeUri(context, treeUri)
                if (dir != null && dir.isDirectory) {
                    val files = dir.listFiles().filter {
                        it.isFile && (it.name?.endsWith(".xlsx") == true || it.name?.endsWith(".xls") == true)
                    }
                    _excelFiles.value = files
                } else {
                    _excelFiles.value = emptyList()
                    _message.value = "Directorio no válido."
                }
            } catch (e: Exception) {
                _message.value = "Error al leer directorio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun processExcelFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.processExcelAndSave(context, uri)
            _isLoading.value = false
            
            if (result.isSuccess) {
                _message.value = "¡Archivo procesado y guardado con éxito!"
            } else {
                _message.value = "Error al procesar: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}

class StoreViewModelFactory(private val repository: StoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
