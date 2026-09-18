package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchAuditDao {
    @Query("SELECT * FROM match_audits ORDER BY timestamp DESC")
    fun getAllAudits(): Flow<List<MatchAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: MatchAuditEntity): Long

    @Query("DELETE FROM match_audits WHERE id = :id")
    suspend fun deleteAuditById(id: Long)

    @Query("DELETE FROM match_audits")
    suspend fun clearAllAudits()
}
