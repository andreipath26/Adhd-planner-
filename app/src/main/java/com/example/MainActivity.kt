package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainScreen
import com.example.ui.PlannerViewModel
import com.example.ui.theme.FocusFlowTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      FocusFlowTheme {
        val viewModel: PlannerViewModel = viewModel()
        MainScreen(viewModel = viewModel)
      }
    }
  }
}

