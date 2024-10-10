package org.dbs.consts

import org.dbs.application.core.service.funcs.LocalDateFuncs.toInt
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object SysConst {

    const val MN42 = 42
    const val MN42L = 42L
    const val SB_ROW_CAPACITY = 200
    const val SB_DEF_CAPACITY = 2000
    const val APP_SHUTDOWN_DELAY = 2000L
    const val DEF_EXIT_PROCESS = -1
    const val SLASH = "/"
    const val MASK_ALL = "all"
    const val ABUSED_QP_DEF = ".html"


    const val VERSION_1_0_0 = "1.0.0"
    const val CRLF = "\n"
    const val NOT_DEFINED = "?"
    const val KEY_NOT_DEFINED = "KEY NOT DEFINED"
    const val UNKNOWN = "UNKNOWN"
    const val NOT_ASSIGNED = "NOT ASSIGNED"
    const val NULL = "null"
    const val ZERO_STRING = "0"
    const val EMPTY_STRING = ""
    const val BLANK_SYMBOL = " "
    const val DATETIME_MS_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS"
    const val DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss"
    const val DATE_FORMAT = "dd.MM.yyyy"
    const val TIME_FORMAT = "HH:mm:ss"
    const val TIME_FORMAT_MS = "HH:mm:ss.SSS"
    const val APP_LOCALE = "ru"
    const val LOG_PKG_NAME = "root-service"
    val FORMAT_dd_MM_yyyy__HH_mm_ss = DateTimeFormatter.ofPattern(DATETIME_FORMAT)
    val FORMAT_dd_MM_yyyy = DateTimeFormatter.ofPattern(DATE_FORMAT)

    const val SSL = true
    const val NO_SSL = !SSL
    const val K8S_MODE = "server.k8sMode"
    val ONE_SECOND = 1.seconds.toJavaDuration()
    val TEN_SECONDS = 10.seconds.toJavaDuration()
    const val MILLIS_1000 = 1000L
    const val TIMEOUT_10000_MILLIS = 10000
    const val TIMEOUT_10000_MILLIS_LONG = 10000L
    val TIMEOUT_30_SEC = 30.seconds.toJavaDuration()
    val TIMEOUT_60_SEC = 60.seconds.toJavaDuration()
    val TIMEOUT_120_SEC = 120.seconds.toJavaDuration()
    const val ONE_ATTEMPT = 1
    const val TWO_ATTEMPTS = 2
    const val FIVE_ATTEMPTS = 5
    const val RESOURCE_FLD = "src/main/resources"
    const val DOCKER_FOLDER = "/opt/"
    val KOTLIN_VERSION = "KotlinVersion = ${KotlinVersion.CURRENT}, JavaVersion = ${Runtime.version()}"

    //==========================================================================
    const val ACTUAL = "ACTUAL"
    const val LONG_ZERO = 0L
    const val LONG_ONE = 1L
    const val SERVICE_USER_ID = LONG_ONE
    val INT_NULL: Int? = null
    val LONG_NULL: Long? = null
    const val INT_10POW9 = 1000000000
    const val INTEGER_ZERO = 0
    const val INTEGER_ONE = 1
    const val DOUBLE_ZERO: Double = 0.0
    val INTEGER_NULL: Int? = null
    val LOCALDATE_NULL: LocalDate? = null
    val OPERDATE_NULL: OperDate? = null
    val OPERDATE_BASE = LONG_ZERO.toLocalDateTime()
    val LOCALDATETIME_NULL: LocalDateTime? = null
    val LOCALTIME_BASE = LONG_ZERO.toLocalDateTime().toLocalTime()

    val MIN_DATE_TIME = LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0)
    val MAX_DATE_TIME = LocalDateTime.of(2100, Month.JANUARY, 1, 0, 0)
    val MIN_DATE_TIME_LONG = LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0).toLong()
    val MAX_DATE_TIME_LONG = LocalDateTime.of(2100, Month.JANUARY, 1, 0, 0).toLong()
    val MIN_DATE_INT = LocalDate.of(1900, Month.JANUARY, 1).toInt()
    val MAX_DATE_INT = LocalDate.of(2100, Month.JANUARY, 1).toInt()

    val BOOLEAN_NULL: BooleanNull = null
    val BOOLEAN_DEFAULT: Boolean = false

    object HotSpotConsts {
        const val VM_XSS = "ThreadStackSize"
        const val VM_XMS = "InitialHeapSize"
        const val VM_XMM = "MaxHeapSize"
        const val VM_MDM = "MaxDirectMemorySize"

        val VMS = setOf(VM_XSS, VM_XMS, VM_XMM, VM_MDM)

    }

    object UsersConsts {
        const val ROOT_USER = "ROOT"
        const val ROOT_USER_NAME = "Manager with all rights"
        const val ROOT_USER_PASS = "ROOT-ADMIN-256"
        const val FAKED_USER = "#UnknownFakedUser#"
    }

    object OutOfServiceConsts {
        val OUT_OF_SERVICE_START_DATE = null
        val OUT_OF_SERVICE_FINISH_DATE = now().toLong()
        const val OUT_OF_SERVICE_NOTE = "Default out of service"
    }

    object BytesConsts {
        const val BYTES_10 = 10
        const val BYTES_100 = 100
        const val BYTES_1K = 1000
    }

    object IntConsts {
        const val FIVE = 5
        const val FIFTY = 50
        const val STR64 = 64
    }

    object LongConsts {
        const val MAX_EXPIRY_TIME = 5000000L
    }

    object DateTimeConsts {
        const val MS_SEC = 1000
        const val MS = 60000
        const val HMS = 3600000
        const val msBorder = 10000
        const val secBorder = 300000
        const val hourBorder = 3600000
    }

    object TestConsts {
        const val EXAMPLE_COM = "https://example.com"
        const val PRODUCT_IMAGES = "{\"name\":\"somepi_cture1\",\"url\":\"http://somepi-cture1.jpg\",\"isTitle\":true}"
    }

    object ThrowableConsts {
        const val SB_THROWABLE_MSG_LENGTH = 10000
    }

    val MONEY_NULL: Money? = null
    val MONEY_ZERO = Money.valueOf(0)
    val MONEY_1M = Money.valueOf(1000000)
    val MAX_MONEY = Money.valueOf(19760000)
    const val UNCHECKED_CAST = "UNCHECKED_CAST"
    const val SECURED = "[SECURED]"
    const val USE_COURUTINE_FUNCS = "Use coroutine functional"
    const val USE_COURUTINE_REPO = "Use coroutine repo"

    //public static final Boolean TEST_MODE = true;
    val CLASS_NULL: Class<*>? = null
    val OBJECT_NULL: Any? = null
    val STRING_NULL: String? = null
    const val STRING_ONE: String = "1"
    const val STRING_ZERO: String = "0"
    const val STRING_TEN: String = "10"
    const val STRING_TRUE = "true"
    const val STRING_FALSE = "false"
    val BYTES_NULL: ByteArray? = null

    //==========================================================================
    val VOID_CLASS = Void::class.java
    val BIGDECIMAL_CLASS = BigDecimal::class.java
    val LOCALDATE_CLASS = LocalDate::class.java
    val LOCALDATETIME_CLASS = LocalDateTime::class.java
    val STRING_CLASS = String::class.java
    val BOOLEAN_CLASS = Boolean::class.java
    val INTEGER_CLASS = Int::class.java
    val LONG_CLASS = Long::class.java

    //==========================================================================
    const val ALL_PACKAGES = "org.dbs"
    const val ALL_PACKAGES_DEF = "org.dbs.*"
    const val MODEL_PACKAGE = ALL_PACKAGES + ".model"
    const val MODEL_ACTION_PACKAGE = MODEL_PACKAGE + ".action"
    const val ENTITY_PACKAGE = ALL_PACKAGES + ".entity"
    const val ENTITY_ACTIONS_PACKAGE = ENTITY_PACKAGE + ".action"
    const val REACTOR_PACKAGE = ALL_PACKAGES + ".entity.reactor"
    const val TARIFF_PACKAGE = ALL_PACKAGES + ".tariff"
    const val REPOSITORY_PACKAGE = ALL_PACKAGES + ".repository"
    const val REPOSITORY_PACKAGE_SHORT = ALL_PACKAGES + ".repo"
    const val COMPONENT_PACKAGE = ALL_PACKAGES + ".component"
    const val CONTROLLER_PACKAGE = ALL_PACKAGES + ".controller"
    const val RSOCKET_PACKAGE = ALL_PACKAGES + ".rsocket"
    const val WEBSOCKET_PACKAGE = ALL_PACKAGES + ".websocket"
    const val SERVICE_PACKAGE = ALL_PACKAGES + ".service"
    const val RESTFUL_PACKAGE = ALL_PACKAGES + ".rest"
    const val SECURITY_PACKAGE = ALL_PACKAGES + ".entity.security"
    const val REFERENCE_PACKAGE = ALL_PACKAGES + ".references"
    const val EXCEPTION_PACKAGE = ALL_PACKAGES + ".exception"
    const val APP_PROPERTIES = "classpath:application.properties"
    const val YML_PROPERTIES = "classpath:application.yml"
    //==========================================================================

    const val APP_ALL = "all"
    const val APP_OUT_OF_SERVICE = "out-of-service"
    const val APP_OUT_OF_SERVICE_CAMEL_CASE = "Out-Of-Service"
    const val APP_SMART_SAFE_SCHOOL = "smart-safe-school"
    const val APP_SMART_SAFE_SCHOOL_CAMEL_CASE = "Smart-Safe-School"
    const val APP_BANKING = "banking"
    const val APP_BANKING_CORE = APP_BANKING + "-core"
    const val APP_JWT_VERIFY = "jwt verify"
    const val APP_CHESS_COMMUNITY = "chess community"
    const val APP_CC_CAMEL_CASE = "ChessCommunity"
    const val APP_SAND_BOX = "chess sandbox"
    const val APP_SAND_BOX_CASE = "ChessSandBox"
    const val APP_INDUSTRIAL_GOODS = "industrial goods"
    const val APP_INDUSTRIAL_GOODS_CASE = "IndustrialGoods"


    // Swagger consts
    //==================================================================================================================
    const val SWG_BR = "<br />"
    const val SWG_REQUIRED_PRIVILEGES = "```Required privileges```"
    const val SWG_MANDATORY_FIELDS = "```Mandatory DTO fields```"
    const val SWG_FONT_RED = """<font color="red"> """
    const val SWG_CLOSE_FONT_TAG = "</font >"
    const val SWG_PV_MANAGER_MODIFY_ALL = "MANAGER_MODIFY_ALL"
    const val SWG_PV_MANAGER_VIEW_ALL = "MANAGER_VIEW_ALL"
    const val SWG_PV_MANAGER_MODIFY_VENDORS = "MANAGER_MODIFY_VENDORS"
    const val SWG_PV_AUTH_SERVICE_ONLY = "AUTH_SERVICE_ONLY"
    const val SWG_MEDIA_FILE = "MEDIA_FILE"
    const val SECURITY_SCHEME_NAME = "bearerAuth"
    const val JWT_NAME = "JWT"
    const val BEARER_LOWER = "bearer"

    val abcCyr = charArrayOf(
        ' ',
        'а',
        'б',
        'в',
        'г',
        'д',
        'ѓ',
        'е',
        'ж',
        'з',
        'ѕ',
        'и',
        'ј',
        'к',
        'л',
        'љ',
        'м',
        'н',
        'њ',
        'о',
        'п',
        'р',
        'с',
        'т',
        'ќ',
        'у',
        'ф',
        'х',
        'ц',
        'ч',
        'џ',
        'ш',
        'А',
        'Б',
        'В',
        'Г',
        'Д',
        'Ѓ',
        'Е',
        'Ж',
        'З',
        'Ѕ',
        'И',
        'Ј',
        'К',
        'Л',
        'Љ',
        'М',
        'Н',
        'Њ',
        'О',
        'П',
        'Р',
        'С',
        'Т',
        'Ќ',
        'У',
        'Ф',
        'Х',
        'Ц',
        'Ч',
        'Џ',
        'Ш',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '/',
        '-'
    )
    val abcLat = arrayOf(
        " ",
        "a",
        "b",
        "v",
        "g",
        "d",
        "]",
        "e",
        "zh",
        "z",
        "y",
        "i",
        "j",
        "k",
        "l",
        "q",
        "m",
        "n",
        "w",
        "o",
        "p",
        "r",
        "s",
        "t",
        "'",
        "u",
        "f",
        "h",
        "c",
        ";",
        "x",
        "{",
        "A",
        "B",
        "V",
        "G",
        "D",
        "}",
        "E",
        "Zh",
        "Z",
        "Y",
        "I",
        "J",
        "K",
        "L",
        "Q",
        "M",
        "N",
        "W",
        "O",
        "P",
        "R",
        "S",
        "T",
        "KJ",
        "U",
        "F",
        "H",
        "C",
        ":",
        "X",
        "{",
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "k",
        "l",
        "m",
        "n",
        "o",
        "p",
        "q",
        "r",
        "s",
        "t",
        "u",
        "v",
        "w",
        "x",
        "y",
        "z",
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "/",
        "-"
    )
}
