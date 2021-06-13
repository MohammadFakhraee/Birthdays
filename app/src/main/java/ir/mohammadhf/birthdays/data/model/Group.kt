package ir.mohammadhf.birthdays.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var color: Int
)