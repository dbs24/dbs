package org.dbs.application.core.service.funcs


import org.dbs.consts.NoArg2String
import org.dbs.consts.SysConst.UNCHECKED_CAST
import java.lang.reflect.ParameterizedType

object GenericFuncs {
    fun <T> getTypeParameterClass(parametrizedType: Class<T>) = getTypeParameterClass(parametrizedType, 0)

    //==========================================================================
    @JvmStatic
    @Suppress(UNCHECKED_CAST)
    fun <T> getTypeParameterClass(parametrizedType: Class<T>, paramNum: Int) = parametrizedType.run {
        val type = genericSuperclass
        val paramType = type as ParameterizedType
        paramType.actualTypeArguments[paramNum] as Class<T>
    }

    //------------------------------------------------------------------------------------------------------------------
    fun <T> requireEquals(val1: T, val2: T, lazyMessage: NoArg2String) = require(val1 == val2) {
        "${lazyMessage()}: ['$val1' <> '${val2}']"
    }

    fun <T> requireIsNull(val1: T, lazyMessage: NoArg2String) = require(val1.hashCode() == 0) {
        "${lazyMessage()}: [applied value should be null]"
    }
}
