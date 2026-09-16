package com.tyejaedon.aipriceoptimization.feature.splash

sealed interface SplashState {
    data object Loading : SplashState
    data object Ready : SplashState

    // TODO(Phase 2): replace Ready with Authenticated / Unauthenticated once
    // Firebase Authentication session restoration is wired in, and add
    // ConfigurationError(message: String) for unrecoverable startup failures.
}

