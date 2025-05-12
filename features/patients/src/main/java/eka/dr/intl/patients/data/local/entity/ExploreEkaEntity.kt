package eka.dr.intl.patients.data.local.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import eka.dr.intl.patients.data.local.convertors.Converters
import org.json.JSONObject

@Keep
@Entity(tableName = "explore_eka_table")
@TypeConverters(Converters::class)
data class ExploreEkaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "icon")
    val icon: String? = null,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "group")
    val group: String? = null,

    @SerializedName("pageid")
    @ColumnInfo(name = "pageid")
    val pageid: String? = null,

    @ColumnInfo(name = "params")
    var params: Map<String, Any>? = null,

    @ColumnInfo(name = "tags")
    val tags: List<String>? = null
)

fun Map<String, Any>.toJSONObject(): JSONObject {
    val jsonObject = JSONObject()
    for ((key, value) in this) {
        jsonObject.put(key, value)
    }
    return jsonObject
}