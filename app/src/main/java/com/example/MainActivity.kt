package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ui.KrishiViewModel
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.CropDiseaseScannerTheme
import com.example.ui.viewmodel.ScannerViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    private val scannerViewModel: ScannerViewModel by viewModels()
    private val krishiViewModel: KrishiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentSeason by krishiViewModel.currentAgriSeason.collectAsState()
            CropDiseaseScannerTheme(agriSeason = currentSeason) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScannerScreen(
                        scannerViewModel = scannerViewModel,
                        krishiViewModel = krishiViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
