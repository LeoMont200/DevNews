package com.avanade.devnews.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avanade.devnews.data.local.dao.FavoriteNewsDao
import com.avanade.devnews.data.local.entity.FavoriteNewsEntity

@Database(
    entities = [FavoriteNewsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DevNewsDatabase : RoomDatabase() {
    abstract fun favoriteNewsDao(): FavoriteNewsDao
}
