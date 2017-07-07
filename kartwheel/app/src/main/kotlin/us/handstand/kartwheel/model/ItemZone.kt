package us.handstand.kartwheel.model

import android.content.ContentValues
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import us.handstand.kartwheel.model.ItemZoneModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent


@AutoValue
abstract class ItemZone : ItemZoneModel, Insertable {
    override fun tableName(): String {
        return ItemZoneModel.TABLE_NAME
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, ItemZoneModel.ID, id())
            putIfNotAbsent(cv, ItemZoneModel.ACTIVEBLOCKCOUNT, activeBlockCount())
            putIfNotAbsent(cv, ItemZoneModel.COURSEID, courseId())
            putIfNotAbsent(cv, ItemZoneModel.LATITUDE, latitude())
            putIfNotAbsent(cv, ItemZoneModel.LONGITUDE, longitude())
            putIfNotAbsent(cv, ItemZoneModel.UPDATEDAT, updatedAt())
            return cv
        }

    companion object : Creator<ItemZone> by Creator(::AutoValue_ItemZone) {
        val FACTORY = ItemZoneModel.Factory<ItemZone>(Creator { id, activeBlockCount, courseId, latitude, longitude, updatedAt -> create(id, activeBlockCount, courseId, latitude, longitude, updatedAt) })
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<ItemZone> {
            return AutoValue_ItemZone.GsonTypeAdapter(gson)
        }
    }
}