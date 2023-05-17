package sem.ua.slot.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    val name: String,
    var balance: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}