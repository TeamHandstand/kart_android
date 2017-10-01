package us.handstand.kartwheel.model


import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import us.handstand.kartwheel.model.PointModel.Creator

@AutoValue
abstract class Point : PointModel, Comparable<Point> {
    override fun compareTo(other: Point): Int = stepOrder()!!.compareTo(other.stepOrder()!!)

    companion object : Creator<Point> by Creator(::AutoValue_Point) {
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Point> = AutoValue_Point.GsonTypeAdapter(gson)
    }
}
