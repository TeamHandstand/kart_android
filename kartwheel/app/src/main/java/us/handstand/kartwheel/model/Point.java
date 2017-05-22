package us.handstand.kartwheel.model;


import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Point implements PointModel, Comparable<Point> {
    @Override
    public int compareTo(@NonNull Point o) {
        return stepOrder().compareTo(o.stepOrder());
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Point> typeAdapter(Gson gson) {
        return new AutoValue_Point.GsonTypeAdapter(gson);
    }
}
