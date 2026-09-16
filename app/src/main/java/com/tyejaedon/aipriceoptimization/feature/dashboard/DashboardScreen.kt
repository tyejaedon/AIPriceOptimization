package com.tyejaedon.aipriceoptimization.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Dashboard placeholder (see docs/Mobile_Blueprint.md, section 6.4).
 * "Create a recommendation" is intentionally inert until Phase 4 (pricing
 * workflow) lands; only the health indicator is wired to a live network
 * call today.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Price Optimization") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HealthStatusChip(
                isChecking = uiState.isCheckingHealth,
                isAvailable = uiState.isServiceAvailable
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Create a recommendation",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Estimate a fair hourly rate using your skills, market, " +
                            "and client location.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthStatusChip(isChecking: Boolean, isAvailable: Boolean?) {
    val label = when {
        isChecking -> "Checking pricing serviceâ€¦"
        isAvailable == true -> "Pricing service available"
        else -> "Pricing service unavailable"
    }
    AssistChip(onClick = {}, label = { Text(label) })
}
