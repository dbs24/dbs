package org.dbs.application.core.service.funcs

import org.dbs.consts.SysConst.UNCHECKED_CAST
import kotlin.reflect.KClass

@Suppress(UNCHECKED_CAST)
object AnnotationFuncs {
    fun <T> isAnnotated(checkedClass: Class<*>, requiredAnnotation: Class<T>) =
        checkedClass.getAnnotation(requiredAnnotation as Class<Annotation>).hashCode() != 0
}
