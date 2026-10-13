package com.avanade.devnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.avanade.devnews.data.local.entity.FavoriteNewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavorite(news: FavoriteNewsEntity)

    @Query(
        """
        DELETE FROM favorite_news
        WHERE userId = :userId AND articleUrl = :articleUrl
        """
    )
    suspend fun removeFavorite(userId: String, articleUrl: String)

    @Query(
        """
        SELECT * FROM favorite_news
        WHERE userId = :userId
        ORDER BY publishedAt DESC
        """
    )
    fun observeFavorites(userId: String): Flow<List<FavoriteNewsEntity>>

    @Query(
        """
        SELECT articleUrl FROM favorite_news
        WHERE userId = :userId
        """
    )
    fun observeFavoriteUrls(userId: String): Flow<List<String>>
}
