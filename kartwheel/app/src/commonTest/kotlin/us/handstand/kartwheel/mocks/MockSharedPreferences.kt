package us.handstand.kartwheel.mocks


import android.content.SharedPreferences

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

/**
 * Robolectric implementation of [android.content.SharedPreferences].
 */
class MockSharedPreferences(content: MutableMap<String, Map<String, Any>>,
                            protected var filename: String, var mode: Int) : SharedPreferences {
    var content: Map<String, Map<String, Any>>

    private val listeners: ArrayList<SharedPreferences.OnSharedPreferenceChangeListener>

    init {
        this.content = content
        if (!content.containsKey(filename)) {
            content.put(filename, HashMap<String, Any>())
        }

        listeners = ArrayList<SharedPreferences.OnSharedPreferenceChangeListener>()
    }

    override fun getAll(): Map<String, *> {
        return HashMap(content[filename])
    }

    override fun getString(key: String, defValue: String?): String? {
        return getValue(key, defValue as Any) as String
    }

    private operator fun getValue(key: String, defValue: Any): Any {
        val fileHash = content[filename]
        if (fileHash != null) {
            val value = fileHash[key]
            if (value != null) {
                return value
            }
        }
        return defValue
    }

    override fun getInt(key: String, defValue: Int): Int {
        return getValue(key, defValue) as Int
    }

    override fun getLong(key: String, defValue: Long): Long {
        return getValue(key, defValue) as Long
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return getValue(key, defValue) as Float
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return getValue(key, defValue) as Boolean
    }

    override fun contains(key: String): Boolean {
        return content[filename]!!.containsKey(key)
    }

    override fun edit(): SharedPreferences.Editor {
        return TestSharedPreferencesEditor()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        if (!listeners.contains(listener))
            listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        if (listeners.contains(listener))
            listeners.remove(listener)
    }

    fun hasListener(listener: SharedPreferences.OnSharedPreferenceChangeListener): Boolean {
        return listeners.contains(listener)
    }

    private inner class TestSharedPreferencesEditor : SharedPreferences.Editor {

        internal var editsThatNeedCommit: MutableMap<String, Any> = HashMap()
        internal var editsThatNeedRemove: MutableSet<String> = HashSet()
        private var shouldClearOnCommit = false

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            if (value == null) {
                editsThatNeedRemove.add(key)
            } else {
                editsThatNeedCommit.put(key, value)
                editsThatNeedRemove.remove(key)
            }
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            editsThatNeedCommit.put(key, value)
            editsThatNeedRemove.remove(key)
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            editsThatNeedCommit.put(key, value)
            editsThatNeedRemove.remove(key)
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            editsThatNeedCommit.put(key, value)
            editsThatNeedRemove.remove(key)
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            editsThatNeedCommit.put(key, value)
            editsThatNeedRemove.remove(key)
            return this
        }

        override fun putStringSet(key: String, value: Set<String>?): SharedPreferences.Editor {
            if (value == null) {
                editsThatNeedRemove.add(key)
            } else {
                editsThatNeedCommit.put(key, value)
                editsThatNeedRemove.remove(key)
            }
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            editsThatNeedRemove.add(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            shouldClearOnCommit = true
            return this
        }

        override fun commit(): Boolean {
            val previousContent: HashMap<String, Object> = content[filename] as HashMap<String, Object>
            val keysToPassToListeners = ArrayList<String>()

            if (shouldClearOnCommit) {
                previousContent.clear()
            } else {
                for (key in editsThatNeedRemove) {
                    previousContent.remove(key)
                    keysToPassToListeners.add(key)
                }
                editsThatNeedRemove.clear()
            }

            for (key in editsThatNeedCommit.keys) {
                if (editsThatNeedCommit[key] != previousContent[key]) {
                    previousContent.put(key, editsThatNeedCommit[key] as Object)
                    keysToPassToListeners.add(key)
                }
            }
            editsThatNeedCommit.clear()

            for (listener in listeners) {
                for (key in keysToPassToListeners) {
                    listener.onSharedPreferenceChanged(this@MockSharedPreferences, key)
                }
            }

            return true
        }

        override fun apply() {
            commit()
        }
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        val v = getValue(key, defValues as Any) as Set<String>
        return v ?: defValues
    }
}
