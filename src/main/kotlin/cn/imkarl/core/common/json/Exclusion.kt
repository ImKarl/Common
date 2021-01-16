package cn.imkarl.core.common.json

/**
 * 序列化的排除标记
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Exclusion(
    val serialize: Boolean = true,
    val deserialize: Boolean = true
)
