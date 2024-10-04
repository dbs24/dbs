package org.dbs.application.core.service.funcs

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.Money
import org.dbs.consts.RestHttpConsts.URI_HTTPS
import org.dbs.consts.SysConst.BytesConsts.BYTES_10
import org.dbs.consts.SysConst.BytesConsts.BYTES_100
import org.dbs.consts.SysConst.BytesConsts.BYTES_1K
import org.dbs.consts.SysConst.MONEY_1M
import org.dbs.consts.SysConst.MONEY_ZERO
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.util.Locale.getDefault
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * @author Козыро Дмитрий
 */
object TestFuncs {
    private val rand by lazy { Random }

    @JvmStatic
    val officeExistCodes = mutableSetOf<String>()

    @JvmStatic
    val departmentExistCodes = mutableSetOf<String>()

    @JvmStatic
    val postExistCodes = mutableSetOf<String>()

    @JvmStatic
    fun generateDocId() = now().let {"${it.year}${it.dayOfYear}_"} + generateTestString15()

    @JvmStatic
    fun generateRequestCode(userId: String, quizId: String) = generateTestRangeLong(10000, 99999).toString() + "-" +
            generateTestString(27) + "-$userId-$quizId"

    @JvmStatic
    fun generateResponseCode(requestCode: String) = generateTestRangeLong(10000, 99999).toString() + "-" +
            generateTestString(27) + "-${requestCode.substring(5)}"

    @JvmStatic
    fun generateTestString10() = generateTestString(10)

    @JvmStatic
    fun generateTestString15() = generateTestString(15)

    @JvmStatic
    fun generateTestString20() = generateTestString(20)

    @JvmStatic
    fun generateTestCode() = generateTestRangeInteger(100000000, 999999999).toString()

    @JvmStatic
    fun generateTestCode3() = generateTestRangeInteger(100, 999).toString()

    @JvmStatic
    fun generateTestCode3NotContains(existsCodes: Set<String>) = run {
        var code = generateTestCode3()
        while (existsCodes.contains(code)) {
            code = generateTestCode3()
        }
        code
    }

    @JvmStatic
    fun generateTestIban() = "BY" + generateTestRangeInteger(10, 99) + generateTestString(10)

    @JvmStatic
    fun generateTestPhone() = "+" + generateTestRangeInteger(100000, 999999999).toString()

    @JvmStatic
    fun generateTestLogin() = generateTestLogin(11)

    @JvmStatic
    fun generateTestName() = generateTestString(1) + generateTestString(20) + generateTestInteger()

    @JvmStatic
    fun generateTestStringName() = generateTestString(1).uppercase() + generateTestString(20)

    @JvmStatic
    fun generateTestPassword811() =
        generateTestString(1).uppercase(getDefault())
            .plus(generateTestString(1).lowercase(getDefault()))
            .plus(generateTestRangeInteger(10, 99))
            .plus(generateTestString(10))


    fun generateTestString(vararg values: String?) = values[rand.nextInt(values.size)]

    @JvmStatic
    @Deprecated("use generateTestRangeInteger instead")
    fun generateTestInteger(vararg values: Int) = values[rand.nextInt(values.size)]

    fun <T> selectFrom(vararg values: T): T = values[rand.nextInt(values.size)]

    fun <T> selectFrom(values: Collection<T>): T = values.elementAt(rand.nextInt(values.size))

    fun <T> selectRandomCollectionFrom(values: Collection<T>): Collection<T> = createCollection<T>().let { rc ->
        repeat(generateTestRangeInteger(0, values.size)) {
            rc.add(selectFrom(values))
        }
        if (rc.isEmpty()) {
            rc.add(selectFrom(values))
        }
        rc.stream()
            .distinct()
            .toList()
    }

    fun <K, V> selectFrom(map: Map<K, V>): V = selectFrom(map.values)

    @Deprecated("")
    fun generateTestBytes(vararg values: Byte?): Byte? = values[rand.nextInt(values.size)]

    @JvmStatic
    fun generateTestRangeInteger(from: Int, to: Int) = rand.nextInt(to - from + 1) + from

    @JvmStatic
    fun generateTestRangeLong(from: Long, to: Long) = rand.nextLong(to - from + 1) + from

    fun generateTestInteger() = generateTestRangeInteger(1, MAX_VALUE)

    fun generateTestLong() = abs(rand.nextLong())

    fun generateTestLongDate() = generateTestRangeInteger(100000, 1000000).toLong()


    fun generateTestLocalDateTime(): LocalDateTime = let {
        val direct = generateBool()
        val randLocalDateTime: LocalDateTime = if (direct) {
            LocalDateTime.now()
                .plusDays(generateTestRangeInteger(0, 1000).toLong())
                .plusHours(generateTestRangeInteger(0, 1000).toLong())
                .plusMinutes(generateTestRangeInteger(0, 1000).toLong())
        } else {
            LocalDateTime.now()
                .minusDays(generateTestRangeInteger(0, 1000).toLong())
                .minusHours(generateTestRangeInteger(0, 1000).toLong())
                .minusMinutes(generateTestRangeInteger(0, 1000).toLong())
        }
        randLocalDateTime
    }

    private const val DIGITS = "1234567890"
    private const val LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LETTERS_DIGITS = "$LETTERS$DIGITS"
    private val ucRow = LETTERS_DIGITS.toCharArray()
    private val lcRow = LETTERS_DIGITS.lowercase().toCharArray()
    val ucLettersRow = LETTERS.toCharArray()
    val lcLettersRow = LETTERS.lowercase().toCharArray()
    fun generateTestString(strLength: Int, symbols: CharArray = ucRow) = let {
        val randomString = CharArray(strLength)
        for (idx in 0 until strLength) randomString[idx] =
            symbols[rand.nextInt(symbols.size.takeIf { idx > 0 } ?: 26)]
        String(randomString)
    }
    fun generateFirstOrLastName(strLength: Int = 25) = generateTestString(1, ucLettersRow).plus(
        generateTestString(strLength - 1, lcLettersRow)
    )

    fun generateLetterString(strLength: Int = 25) = generateTestString(1, ucLettersRow).plus(
        generateTestString(strLength - 1, lcLettersRow)
    )
    fun generateUpperLetterString(strLength: Int = 25) = generateTestString(strLength, ucLettersRow)

    fun generateTestLogin(strLength: Int) = generateTestString(strLength, lcLettersRow)

    fun generateTestSwift() = generateUpperLetterString(11)

    fun generateTestEmail() = generateLetterString(5) + "@" + generateTestString(6) + "." + generateTestString(3)
    fun generateTestUrl() = URI_HTTPS + generateLetterString(11) + ".org"
    fun generateLong() = rand.nextLong()

    fun generateBirthDateDto() = generateTestRangeInteger(1000000, 2000000).absoluteValue

    fun generateUnsignedLong() = abs(rand.nextLong())

    fun generate100Bytes(): ByteArray = generateBytes(BYTES_100)

    fun generate1000Bytes(): ByteArray = generateBytes(BYTES_1K)

    fun generate10Bytes(): ByteArray = generateBytes(BYTES_10)

    fun generateTestImage(): String = run {
        "{\"name\":\"${generateTestName()}\",\"url\":\"someUrl\"}"
    }

    private fun generateBytes(maxSize: Int): ByteArray = ByteArray(rand.nextInt(maxSize)).also {
        rand.nextBytes(it)
    }

    fun generateBool() = rand.nextBoolean()

    fun generateBigDecimal(min: Money, max: Money, scale: Int = 2): Money =
        min.add(BigDecimal(Math.random()).multiply(max.subtract(min))).setScale(scale, HALF_UP)

    fun generateTestDouble(min: Double, max: Double) =
        min + (Math.random() * (max - min))

    fun generateBigDecimal(): Money =
        MONEY_ZERO.add(BigDecimal(Math.random()).multiply(MONEY_1M.subtract(MONEY_ZERO)))
            .setScale(2, HALF_UP)

}
