package org.dbs.application.core.service.funcs

import org.dbs.application.core.service.funcs.Patterns.EMAIL_PATTERN
import org.dbs.consts.NoArg2Generic
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.consts.SysConst.APP_LOCALE
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.SECURED
import org.dbs.consts.SysConst.abcCyr
import org.dbs.consts.SysConst.abcLat
import java.util.*
import java.util.Locale.getDefault
import java.util.UUID.randomUUID
import java.util.function.Predicate
import java.util.stream.Stream

object StringFuncs {
    val locale = Locale(APP_LOCALE)

    //==========================================================================
    @JvmStatic
    fun createRandomString(limit: Int) =
        randomUUID().toString().plus(randomUUID().toString()).plus(randomUUID().toString())
            .replace("-".toRegex(), "")
            .substring(0, limit).uppercase(getDefault())

    fun String.clearName() = if (this.indexOf("$$") > 0) this.substring(
        0, this.indexOf("$$")
    ) else this

    fun String.secureReplace(securedParams: String) = Predicate { param: String ->
        Stream.of(*securedParams.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()).filter { key: String -> param.contains(key.trim { it <= ' ' }.lowercase()) }
            .count() > 0
    }.also { ti ->
        (this.substring(0, this.indexOf("=")) + "=$SECURED").takeIf { ti.test(this) } ?: this
    }

    fun <T> String?.ifNotEmpty(func: NoArg2Generic<T>): T? =
        this?.let {
            if (this != EMPTY_STRING) func() else null
        }

    fun String?.isNull(): Boolean = (this.hashCode() == 0)
    fun String?.isNotNull(): Boolean = (this.hashCode() != 0)

    //==================================================================================================================
    fun String.last40() = lastN ({ 40 })
    fun String.last15() = lastN ({ 15 })

    inline fun String.lastN(lengthLimit: () -> Int, maskSymbol: String = ".") = let {
        val limit = lengthLimit()
        if (it.length > limit) "$maskSymbol$maskSymbol$maskSymbol${it.substring(it.length - limit)}" else it
    }

    fun String.first15() = firstN ({ 15 })
    fun String.first40() = firstN ({ 40 })
    inline fun String.firstN(lengthLimit: () -> Int, maskSymbol: String = ".") = let {
        val limit = lengthLimit()
        if (it.length > limit) "${it.substring(0, limit)}$maskSymbol$maskSymbol$maskSymbol" else it
    }
    fun String.firstAndLast15() = firstAndLastN ({ 15 })
    fun String.firstAndLast3() = firstAndLastN ({ 3 })
    fun String.firstAndLast10() = firstAndLastN ({ 10 })
    fun String.firstAndLast40() = firstAndLastN ({ 40 })
    inline fun String.firstAndLastN(lengthLimit: () -> Int, maskSymbol: String = ".") = let {
        val limit = lengthLimit()
        if (it.length < (limit + 6)) it else
            (it.firstN ({ limit }, maskSymbol).plus(it.lastN ({ limit }, maskSymbol))).plus(it.symbols())
    }

    fun String.convert2Cyrilic() = StringBuilder().let { sb ->
        for (element in this) {
            for (x in abcCyr.indices) {
                if (element == abcCyr[x]) {
                    sb.append(abcLat[x])
                }
            }
        }
        sb.toString()
    }

    fun String?.e2null() = this?.let { it.ifEmpty { null } }

    fun String.symbols() = " (${this.length} symbols)"

    fun String.update(vararg pairs: Pair<String, String>) =
        update(pairs as Collection<Pair<String, String>>)

    fun String.update(pairs: Collection<Pair<String, String>>) =
        pairs.fold(this) { acc, (old, new) -> acc.replace(old, new, ignoreCase = true) }


    fun String.encode(key: String) = StringBuilder(this.length).let { sb ->
        fun Int.encode(mask: Int): Int = (this xor mask)//.also { println (" ${this.toChar()} (${this}) -> $it")   }
        var xorMask = key.hashCode()
        for (i in indices) {
            sb.append(this[i].code.encode(xorMask).toChar())
            xorMask = if (i % 2 == 0)
                xorMask.hashCode() shr 1
            else
                xorMask.hashCode() shl 1
        }
        sb.toString()
    }

    fun String.validateEmail(entityName: String): String = EMAIL_PATTERN.matcher(this).matches().let {
        require(it) { "validateEmail($entityName): invalid email: '$this'" }
        this
    }

    fun String.getJwtFromBearer() = if (this.length > BEARER.length) this.substring(BEARER.length) else this

    fun String.getImageFileName() = this.indexOf("?").let {
        if (it > 0) this.substring(it) else this
    }

    fun String?.emptyIfNull(): String = this ?: EMPTY_STRING

    fun String.toSearchMask(): String = "${this.trim().lowercase()}%"

    fun String?.needValidateCode(newCode: String): Boolean = this?.let { it != newCode } ?: true

    fun String?.takeFileName(): String? = this?.split("/")?.last()

    /**
     * Take bucket nme from minio relative path (format: (bucketName/fileName)
     */
    fun String?.takeBucketName(): String? = this?.split("/")?.let { it[it.lastIndex - 1] }

    fun String?.start(testVal: String): Boolean = this?.startsWith(testVal, true) ?: false

    fun String?.ends(testVal: String): Boolean = this?.endsWith(testVal, true) ?: false

    fun String.any2Empty(): String = this.takeUnless { it == "any" } ?: EMPTY_STRING

    fun String.cutAnyMeasure(): String = this.replace("_ANY", EMPTY_STRING)
    fun String.addAnyMeasure(): String = "${this.cutAnyMeasure()}_ANY"

    fun String.camelNotation(): String = camelNotation(" ", "_")

    fun String.camelNotation(vararg replacement: String): String =
        this.split(*replacement).joinToString(" ") { it.lowercase().replaceFirstChar { it.uppercase() } }

    fun String.truncate(maxSize: Int = 100): String = if (this.length > maxSize) this.substring(1, maxSize).plus("...") else this

}
