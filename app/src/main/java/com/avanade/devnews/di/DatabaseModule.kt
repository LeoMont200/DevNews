package com.avanade.devnews.di

import android.content.Context
import androidx.room.Room
import com.avanade.devnews.data.local.dao.FavoriteNewsDao
import com.avanade.devnews.data.local.database.DevNewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDevNewsDatabase(
        @ApplicationContext context: Context
    ): DevNewsDatabase {
        return Room.databaseBuilder(
            context,
            DevNewsDatabase::class.java,
            "devnews.db"
        ).build()
    }

    @Provides
    fun provideFavoriteNewsDao(
        database: DevNewsDatabase
    ): FavoriteNewsDao {
        return database.favoriteNewsDao()
    }
}
