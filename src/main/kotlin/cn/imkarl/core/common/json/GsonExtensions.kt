package cn.imkarl.core.common.json

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder


/**
 * 添加字段的排除规则
 */
fun GsonBuilder.addFieldExclusionStrategy(shouldSkipField: (field: FieldAttributes, isSerialize: Boolean) -> Boolean): GsonBuilder {
    this.addSerializationExclusionStrategy(object : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes?): Boolean {
            f?.let {
                return shouldSkipField.invoke(it, true)
            }
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return false
        }
    }).addDeserializationExclusionStrategy(object : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes?): Boolean {
            f?.let {
                return shouldSkipField.invoke(it, false)
            }
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return false
        }
    })
    return this
}

/**
 * 添加类的排除规则
 */
fun GsonBuilder.addClassExclusionStrategy(shouldSkipClass: (clazz: Class<*>, isSerialize: Boolean) -> Boolean): GsonBuilder {
    this.addSerializationExclusionStrategy(object : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes?): Boolean {
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            clazz?.let {
                return shouldSkipClass.invoke(it, true)
            }
            return false
        }
    }).addDeserializationExclusionStrategy(object : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes?): Boolean {
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            clazz?.let {
                return shouldSkipClass.invoke(it, false)
            }
            return false
        }
    })
    return this
}
