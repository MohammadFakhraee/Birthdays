package ir.mohammadhf.birthdays.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gift_frames")
data class StoryFrame(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val name: String,
    val category: String,
    val numberOfPictures: Int,
    var previewPath: String,
    var framePath: String
)