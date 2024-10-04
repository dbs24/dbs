package org.dbs.validator

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection


interface Validator<T> {
    //    public default Collection<ErrorInfo> validate(T t) {
    //        return ServiceFuncs.<ErrorInfo>createCollection();
    //    }
    fun warn(t: T): Collection<WarnInfo?>? = createCollection()


    fun buildErrorCollection(): MutableCollection<ErrorInfo> = createCollection()

}
