package com.tyejaedon.aipriceoptimization.di

import com.tyejaedon.aipriceoptimization.data.repository.HealthRepositoryImpl
import com.tyejaedon.aipriceoptimization.domain.repository.HealthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds domain repository interfaces to their data-layer implementations.
 * Add one @Binds function per repository as new features land
 * (AuthRepository, PricingRepository, ProfileRepository, HistoryRepository).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHealthRepository(impl: HealthRepositoryImpl): HealthRepository
}

