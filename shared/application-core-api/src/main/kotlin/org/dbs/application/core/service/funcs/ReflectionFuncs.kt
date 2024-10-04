package org.dbs.application.core.service.funcs

import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.lang.reflect.Modifier


@Suppress(UNCHECKED_CAST)
object ReflectionFuncs {
    @JvmStatic
    fun <T : Class<*>> createPkgClassesCollection(packageName: String, rootClazz: T?): Collection<T> = ArrayList(
        rootClazz?.run { Reflections(packageName).getSubTypesOf(this as Class<T>) as Collection<T> }
            ?: run {
                Reflections(packageName)
                    .get(Scanners.SubTypes.of(Object::class.java)) as Collection<T>

            }
    )

    //==========================================================================
    @JvmStatic
    @Suppress(UNCHECKED_CAST)
    fun <T : Class<*>, A : Class<*>> processPkgClassesCollection(
        packageName: String,
        rootClass: T,
        annClass: A,
        servClassProcessor: ServClassProcessor
    ) {

        // значения для справочника берутся из аннотаций классов
        createPkgClassesCollection(packageName, rootClass)
            .stream()
            .filter { p: T -> !p.isInterface }
            .filter { p: T -> !Modifier.isAbstract(p.modifiers) }
            .filter { p: T ->
                AnnotationFuncs.isAnnotated<A>(p, annClass as Class<A>)
            }
            .forEach { refClazz: T -> servClassProcessor.processClass(refClazz as Class<*>) }
    }
}
