package com.weather.core.common.di

import com.weather.core.common.DefaultDispatchersProvider
import com.weather.core.common.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindDispatchersProvider(
        impl: DefaultDispatchersProvider
    ): DispatchersProvider
}
