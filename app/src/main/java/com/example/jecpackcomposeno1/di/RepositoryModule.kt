package com.example.jecpackcomposeno1.di

import com.example.jecpackcomposeno1.ui.theme.domain.repository.StorageRepository
import com.example.jecpackcomposeno1.ui.theme.domain.repository.StorageRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Nối interface -> implementation cho Hilt.
 *
 * @Inject constructor trên [StorageRepositoryImp] chỉ nói "biết cách tạo Imp", nó KHÔNG
 * nói "khi ai xin StorageRepository thì đưa Imp". Cái nối đó là @Binds ở đây — thiếu nó
 * là lỗi Dagger/MissingBinding.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStorageRepository(impl: StorageRepositoryImp): StorageRepository
}