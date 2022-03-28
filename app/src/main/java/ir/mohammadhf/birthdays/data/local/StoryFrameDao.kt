package ir.mohammadhf.birthdays.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ir.mohammadhf.birthdays.data.model.StoryFrame

@Dao
interface StoryFrameDao {

    @Insert
    suspend fun insertOne(storyFrame: StoryFrame)

    @Query("SELECT * FROM gift_frames WHERE category LIKE :category")
    suspend fun getByCategory(category: String): List<StoryFrame>

    @Query("SELECT * FROM gift_frames")
    suspend fun getAll(): List<StoryFrame>

    @Query("SELECT * FROM gift_frames WHERE id = :storyFrameId")
    suspend fun getOne(storyFrameId: Long): StoryFrame
}