package com.tyejaedon.aipriceoptimization.core.navigation

/**
 * Route constants for Navigation Compose.
 *
 * IMPORTANT: never pass a full domain object (e.g. a PricingRecommendation)
 * through a navigation argument. Use a repository-backed identifier instead
 * (see docs/Mobile_Blueprint.md, section "Application navigation").
 */
object NavRoutes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"

    // Reserved for upcoming phases. Add routes here as each feature lands:
    // AUTH_SIGN_IN, AUTH_REGISTER, AUTH_FORGOT_PASSWORD,
    // ONBOARDING_PROFILE, ONBOARDING_COUNTRY,
    // PRICING_NEW, PRICING_RESULT/{requestId},
    // HISTORY, HISTORY_DETAIL/{quoteId},
    // PROFILE, SETTINGS, ABOUT
}

