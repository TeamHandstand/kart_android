package us.handstand.kartwheel.model;


import android.content.ContentValues;

class Util {
    static void putIfNotAbsent(ContentValues cv, String key, String value) {
        if (value != null) {
            cv.put(key, value);
        }
    }

    static void putIfNotAbsent(ContentValues cv, String key, Integer value) {
        if (value != null) {
            cv.put(key, value);
        }
    }

    static void putIfNotAbsent(ContentValues cv, String key, byte[] value) {
        if (value != null) {
            cv.put(key, value);
        }
    }

    static void putIfNotAbsent(ContentValues cv, String key, Long value) {
        if (value != null) {
            cv.put(key, value);
        }
    }

    static void putIfNotAbsent(ContentValues cv, String key, Boolean value) {
        if (value != null) {
            cv.put(key, value);
        }
    }

    static void putIfNotAbsent(ContentValues cv, String key, Double value) {
        if (value != null) {
            cv.put(key, value);
        }
    }
}
