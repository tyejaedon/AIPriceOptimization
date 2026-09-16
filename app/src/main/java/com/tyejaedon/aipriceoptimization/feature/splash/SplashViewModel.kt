package com.tyejaedon.aipriceoptimization.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tyejaedon.aipriceoptimization.domain.usecase.GetHealthStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/**
 * Splash-time responsibilities (see docs/Mobile_Blueprint.md, section 6.1):
 * - Perform a bounded, best-effort API health check.
 * - Never block indefinitely; fall through to the dashboard on timeout so a
 *   temporarily-unknown API status does not strand the user.
 *
 * TODO(Phase 2): initialize Firebase, check session state, and branch into
 * SplashState.Authenticated / Unauthenticated accordingly.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getHealthStatus: GetHealthStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            withTimeoutOrNull(HEALTH_CHECK_TIMEOUT_MS) {
                getHealthStatus()
            }
            _state.value = SplashState.Ready
        }
    }

    private companion object {
        const val HEALTH_CHECK_TIMEOUT_MS = 3_000L
    }
}

