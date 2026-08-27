package krio.systemdesign.shoppingapp.core.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkInterceptor
