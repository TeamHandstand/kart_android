package us.handstand.kartwheel.model


import android.content.ContentValues

internal object Util {
    fun putIfNotAbsent(cv: ContentValues, key: String, value: String?) {
        if (value != null) {
            cv.put(key, value)
        }
    }

    fun putIfNotAbsent(cv: ContentValues, key: String, value: Int?) {
        if (value != null) {
            cv.put(key, value)
        }
    }

    fun putIfNotAbsent(cv: ContentValues, key: String, value: ByteArray?) {
        if (value != null) {
            cv.put(key, value)
        }
    }

    fun putIfNotAbsent(cv: ContentValues, key: String, value: Long?) {
        if (value != null) {
            cv.put(key, value)
        }
    }

    fun putIfNotAbsent(cv: ContentValues, key: String, value: Boolean?) {
        if (value != null) {
            cv.put(key, value)
        }
    }

    fun putIfNotAbsent(cv: ContentValues, key: String, value: Double?) {
        if (value != null) {
            cv.put(key, value)
        }
    }
}
