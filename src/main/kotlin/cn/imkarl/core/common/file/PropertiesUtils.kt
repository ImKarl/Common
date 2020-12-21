package cn.imkarl.core.common.file

import cn.imkarl.core.common.json.JsonUtils
import java.io.File
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Properties工具类
 * @author imkarl
 */
class PropertiesUtils(private val file: File) {

    private val properties: Properties

    init {
        if (!file.exists()) {
            FileUtils.createNewFile(file)
        }
        properties = Properties()
        properties.load(file.inputStream())
    }

    fun getString(key: String): String? {
        return getString(key, null)
    }
    fun getString(key: String, defValue: String?): String? {
        return properties.getProperty(key, defValue)
    }

    inline fun <reified T> get(key: String, defValue: T): T {
        val value = getString(key)
        if (value.isNullOrBlank()) {
            return defValue
        }

        return when(T::class) {
            String::class -> getString(key) ?: defValue
            Boolean::class -> getString(key)?.toBoolean() ?: defValue
            Double::class -> getString(key)?.toDoubleOrNull() ?: defValue
            Float::class -> getString(key)?.toFloatOrNull() ?: defValue
            Int::class -> getString(key)?.toIntOrNull() ?: defValue
            Long::class -> getString(key)?.toLongOrNull() ?: defValue
            else -> try { JsonUtils.fromJson<T>(getString(key)) } catch (throwable: Throwable) { defValue }
        } as T
    }



    /**
     * 获取Editor
     */
    fun edit(): Editor {
        return Editor(properties, file)
    }

    /**
     * 保存数据
     */
    fun put(key: String, value: Any?) {
        edit().put(key, value).commit()
    }

    /**
     * 是否保存key
     *
     * @param key 键
     * @return 是否保存过
     */
    operator fun contains(key: String): Boolean {
        return key.isNotEmpty() && properties.contains(key)
    }

    /**
     * 删除数据
     */
    fun remove(key: String) {
        if (key.isEmpty()) {
            return
        }
        edit().remove(key).commit()
    }

    /**
     * 清空所有数据
     */
    fun clear() {
        edit().clear().commit()
    }


    inline fun <reified T> field(defValue: T): ReadWriteProperty<Any, T> {
        return object: ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                val key = property.name
                return get(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                val key = property.name
                when(T::class) {
                    String::class, Boolean::class, Double::class, Float::class, Int::class, Long::class -> put(key, value)
                    else -> put(key, value?.let { JsonUtils.toJson(value) })
                }
            }
        }
    }
    inline fun <reified T> fieldByKey(key: String, defValue: T): ReadWriteProperty<Any, T> {
        return object: ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return get(key, defValue)
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                when(T::class) {
                    String::class, Boolean::class, Double::class, Float::class, Int::class, Long::class -> put(key, value)
                    else -> put(key, value?.let { JsonUtils.toJson(value) })
                }
            }
        }
    }

    inner class Editor internal constructor(private val properties: Properties, private val file: File) {
        /**
         * 保存数据
         */
        fun put(key: String, value: Any?): Editor {
            if (key.isEmpty()) {
                return this
            }
            if (value == null) {
                return remove(key)
            }

            properties.setProperty(key, value.toString())
            return this
        }

        /**
         * 删除数据
         */
        fun remove(key: String): Editor {
            if (key.isNotEmpty()) {
                properties.remove(key)
            }
            return this
        }

        /**
         * 清空所有数据
         */
        fun clear(): Editor {
            properties.clear()
            return this
        }

        /**
         * 提交修改
         */
        fun commit() {
            properties.store(file.outputStream(), null)
        }
    }

}