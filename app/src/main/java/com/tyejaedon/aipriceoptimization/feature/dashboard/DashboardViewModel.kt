package com.tyejaedon.aipriceoptimization.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tyejaedon.aipriceoptimization.core.common.AppResult
import com.tyejaedon.aipriceoptimization.domain.usecase.GetHealthStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getHealthStatus: GetHealthStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshHealth()
    }

    fun refreshHealth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingHealth = true, error = null) }
            when (val result = getHealthStatus()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isCheckingHealth = false,
                        isServiceAvailable = result.value.isHealthy && result.value.modelsLoaded
                    )
                }

                is AppResult.Failure -> _uiState.update {
                    it.copy(
                        isCheckingHealth = false,
                        isServiceAvailable = false,
                        error = result.error
                    )
                }
            }
        }
    }
}

