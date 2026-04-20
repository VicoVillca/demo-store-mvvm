package com.example.demo_store_mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.demo_store_mvvm.data.local.AppDatabase
import com.example.demo_store_mvvm.data.repository.StoreRepository
import com.example.demo_store_mvvm.presentation.ui.ProductsScreen
import com.example.demo_store_mvvm.presentation.viewmodel.StoreViewModel
import com.example.demo_store_mvvm.presentation.viewmodel.StoreViewModelFactory
import com.example.demo_store_mvvm.ui.theme.AppbasestoreonionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = StoreRepository(database.storeDao())
        val viewModelFactory = StoreViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[StoreViewModel::class.java]

        setContent {
            AppbasestoreonionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProductsScreen(viewModel = viewModel)
                }
            }
        }
    }
}