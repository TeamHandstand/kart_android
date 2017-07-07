package us.handstand.kartwheel.model

import android.content.ContentValues
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import us.handstand.kartwheel.model.DroppedItemModel.Creator
import us.handstand.kartwheel.model.DroppedItemModel.TABLE_NAME
import us.handstand.kartwheel.model.Util.putIfNotAbsent


@AutoValue
abstract class DroppedItem : DroppedItemModel, Insertable {
    override fun tableName(): String {
        return TABLE_NAME
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, DroppedItemModel.ID, id())
            putIfNotAbsent(cv, DroppedItemModel.DROPPEDAT, droppedAt())
            putIfNotAbsent(cv, DroppedItemModel.ITEMID, itemId())
            putIfNotAbsent(cv, DroppedItemModel.LATITUDE, latitude())
            putIfNotAbsent(cv, DroppedItemModel.LONGITUDE, longitude())
            putIfNotAbsent(cv, DroppedItemModel.RACEID, raceId())
            putIfNotAbsent(cv, DroppedItemModel.STEPPEDONAT, steppedOnAt())
            putIfNotAbsent(cv, DroppedItemModel.UPDATEDAT, updatedAt())
            return cv
        }

    companion object : Creator<DroppedItem> by Creator(::AutoValue_DroppedItem) {
        val FACTORY = DroppedItemModel.Factory<DroppedItem>(Creator { id, droppedAt, itemId, latitude, longitude, raceId, steppedOnAt, updatedAt -> create(id, droppedAt, itemId, latitude, longitude, raceId, steppedOnAt, updatedAt) })
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<DroppedItem> {
            return AutoValue_DroppedItem.GsonTypeAdapter(gson)
        }
    }

}