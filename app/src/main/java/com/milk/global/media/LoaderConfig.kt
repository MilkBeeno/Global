package com.milk.global.media

import android.app.Application
import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.memory.MemoryCache
import coil.request.CachePolicy
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

object LoaderConfig {
    private const val MAX_FACTORY_CACHE = 10 * 1024 * 1024L

    fun initialize(application: Application) {
        Coil.setImageLoader(
            ImageLoader.Builder(application)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .memoryCache(createMemoryCache(application))
                .callFactory(createOkHttp(application))
                .build()
        )
    }

    private fun createMemoryCache(application: Application): MemoryCache {
        return MemoryCache
            .Builder(application)
            .maxSizePercent(0.2)
            .weakReferencesEnabled(true)
            .build()
    }

    private fun createOkHttp(application: Application): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(createDefaultCache(application))
            .build()
    }

    private fun createDefaultCache(context: Context): Cache {
        val cacheDirectory = getDefaultCacheDirectory(context)
        return Cache(cacheDirectory, MAX_FACTORY_CACHE)
    }

    private fun getDefaultCacheDirectory(context: Context): File {
        return File(context.cacheDir, "image_cache").apply { mkdirs() }
    }
}