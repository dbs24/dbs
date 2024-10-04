package org.dbs.application.core.service.funcs;

import org.dbs.consts.SysConst.EMPTY_STRING
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

object Patterns {

    const val WHITESPACE_CHARS = ("" /* dummy empty string for homogeneity */
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL)
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD
            + "\\u2001" // EM QUAD
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000") // IDEOGRAPHIC SPACE


    private const val ARABIC_CHARS = "\\p{IsArabic}"
    private const val CYRILLIC_CHARS = "\\p{IsCyrillic}"
    private const val TURKISH_CHARS = "a-zA-ZğüşöçİĞÜŞÖÇ"
    private const val LATIN_CHARS = "\\p{IsLatin}"

    private const val NOTE_GENERIC_PATTER = "0-9\\'\\-,.:;\\s!?()\\{\\}\\[\\]\\u0009\\u0020\\n\\u000D\\u000A"

    val HTTPS_URL_PATTERN by lazy { compile(
        "https?:\\/\\/(www\\.)?" +
                "[a-zA-Z0-9\\.\\_\\%\\-\\:\\/]{1,1024}" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,512}"
    ) }
    val NAME_IMAGE by lazy { compile("[a-zA-Z0-9\\.\\:\\_\\%\\-\\/\\(\\)\\u0020]{0,1024}") }



    val HTTPS_URL_PATTERN_WITH_EXTENTION by lazy { compile(
        "https?:\\/\\/(www\\.)?" +
                "[a-zA-Z0-9\\.\\_\\%\\-\\:\\/]{1,256}" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    ) }

    val STD_STRING_PATTERN_1000 by lazy { buildPatternSet(NOTE_GENERIC_PATTER, 400)  }

    val LEGAL_DOMAIN by lazy { compile("[a-zA-Z0-9\\.\\-]{10,100}") }
    val LEGAL_DOMAIN_PORT by lazy { compile("[a-z\\.\\-]{10,100}[:][0-9]{1,5}") }

    val COUNTRY_CODE_PATTERN by lazy { compile("[A-Z]{2}") }
    val CURRENCY_CODE_PATTERN by lazy { compile("[A-Z]{3}") }
    val REGION_CODE_PATTERN by lazy { compile("[A-Za-z0-9\\-\\.]{1,10}") }

    val TOPIC_CODE_PATTERN by lazy { compile("[A-Z_]{1,50}") }

    val STR4M by lazy { compile("[a-zA-Z0-9$WHITESPACE_CHARS]{4,50}") }
    val STR50 by lazy { compile("[a-zA-Z0-9$WHITESPACE_CHARS]{0,50}") }
    val STR100 by lazy { compile("[a-zA-Z0-9$WHITESPACE_CHARS]{0,100}") }
    val STR200 by lazy { compile("[a-zA-Z0-9$WHITESPACE_CHARS]{0,200}") }
    val STR400 by lazy { compile("[a-zA-Z0-9$WHITESPACE_CHARS]{0,400}") }
    val STR10000 by lazy { compile("[a-zA-Z0-9$WHITESPACE_CHARS]{0,10000}") }

    val STR50L by lazy { compile("[a-zA-Z]{0,50}") }

    val STR2N by lazy { compile("[0-9]{1,2}") }
    val STR3N by lazy { compile("[0-9]{1,3}") }
    val STR4N by lazy { compile("[0-9]{1,4}") }
    val STR10N by lazy { compile("[0-9]{1,10}") }
    val STR50N by lazy { compile("[0-9]{0,50}") }
    val STR100N by lazy { compile("[0-9]{0,100}") }
    val STR400N by lazy { compile("[0-9]{0,400}") }
    val NUM_3 by lazy { compile("[0-9]{1,3}") }
    val NUM_20 by lazy { compile("[0-9]{1,20}") }
    val FLOAT_20 by lazy { compile("[0-9]{0,20}(.|,)[0-9]{0,10}") }

    val STR10U by lazy { compile("[a-zA-Z0-9\\_]{0,10}") }
    val STR50U by lazy { compile("[a-zA-Z0-9\\_]{0,50}") }
    val STR100U by lazy { compile("[a-zA-Z0-9\\_]{0,100}") }
    val STR200U by lazy { compile("[a-zA-Z0-9\\_\\u0020]{0,200}") }
    val STR400U by lazy { compile("[a-zA-Z0-9\\_\\u0020]{0,400}") }

    val SHORT_NAME_PATTERN by lazy { compile("[a-zA-Z0-9\\_\\u0020]{0,100}") }
    val ENTITY_SHORT_STATUS_NAME_PATTERN by lazy { compile("[A-Z\\_]{4,100}") }

    val JWT_PATTERN by lazy { compile(
        "(^[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\$)"
    ) }

    val MONEY_PATTERN by lazy { compile("[0-9]{1,200}\\.?[0-9]{0,4}") }

    val IDs by lazy { compile("[0-9\\,]{0,200}") }
    val IDNotNull by lazy { compile("[0-9]{1,200}") }
    val IDsLong by lazy { compile("[0-9\\,]{0,100000}") }
    val IDsNotZero by lazy { compile("[0-9\\,]{1,100000}") }
    val IDsWithSpace by lazy { compile("[0-9\\u0020]{0,100000}") }
    val ID by lazy { compile("[0-9]{0,200}") }
    val ENTITY_ID by lazy { compile("[0-9]{0,20}") }

    val STR50F by lazy { compile("[a-zA-Z0-9\\.\\,\\:\\;\\_\\%\\-\\(\\)\\u0020]{0,50}") }
    val STR100F by lazy { compile("[a-zA-Z0-9\\.\\,\\:\\;\\_\\%\\-\\(\\)\\u0020]{0,100}") }
    val STR200F by lazy { compile("[a-zA-Z0-9\\.\\,\\:\\;\\_\\%\\-\\(\\)\\u0020]{0,200}") }
    val STR400F by lazy { compile("[a-zA-Z0-9\\.\\,\\:\\;\\_\\%\\-\\(\\)\\u0020]{0,400}") }
    val TEXT400F by lazy { compile("[a-zA-Z0-9\\.\\,\\:\\;\\_\\%\\-\\(\\)$WHITESPACE_CHARS]{0,400}") }
    val TEXT10000F by lazy { compile("[a-zA-Z0-9\\.\\,\\:\\;\\_\\%\\-\\(\\)$WHITESPACE_CHARS]{0,10000}") }
    val TEXT4096F by lazy { compile("^.{3,4096}$") }

    val SEARCH_MASK_PATTERN by lazy { compile("\\w{1,50}") }
    val BOOLEAN_PATTERN by lazy { compile("^(?i)(true|false)$") }

    val LINK_PATTERN by lazy {
        compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)") }
    val CODE_PATTERN by lazy { compile("[0-9]{1,20}") }
    val CODE_WITH_COMMA_PATTERN by lazy { compile("[0-9\\,]{1,500}") }

    //Store--------------------------------------------------------
    val LOGIN_COMMON_PATTERN by lazy { compile("[a-zA-Z0-9\\-\\_\\.\\@]{5,49}")  } // 6-20 symbols
    val LOGIN_PATTERN by lazy { compile("[a-z0-9]{5,19}") } // 6-20 symbols
    val LOGINS_PATTERN by lazy { compile("[a-z0-9,]{5,1000}") } // 6-20 symbols
    val LOGIN_PATTERN_MASK by lazy { compile("[a-z0-9]{1,19}") } // 6-20 symbols
    val PASSWORD_PATTERN by lazy { compile("(?=^.{6,20}$)((?=.*\\d)|(?=.*W+))(?![.\n])(?=.*[A-Z])(?=.*[a-z]).*") }
    val USER_NAME_PATTERN_MASK by lazy { buildPatternSet("\\-", 50) }
    val USER_FIRST_NAME_PATTERN by lazy { buildPatternSet("\\-\\u0020", 80) }
    val USER_LAST_NAME_PATTERN by lazy { buildPatternSet("\\-\\u0020", 80) }
    val USER_NAME_PATTERN by lazy { buildPatternSet(EMPTY_STRING, 50) }
    val NAME_PATTERN by lazy { buildPatternSet("0-9\\s", 2, 100) }
    val NAME_PATTERN_500 by lazy { buildPatternSet("0-9\\s", 2, 500) }
    val NAME_PATTERN_MASK by lazy { compile("[a-zA-Z0-9\\s]{1,99}") } // 3-100 symbols

    val SERVICE_GROUP_NAME_PATTERN by lazy { buildPatternSet("0-9\\s", 2, 99) }
    val SERVICE_GROUP_CODE_PATTERN by lazy { compile("[a-zA-Z]{7}-[0-9]{8}-[a-zA-Z]{5}") }
    val SERVICE_GROUP_NAME_MASK_PATTERN by lazy { buildPatternSet("[a-zA-Z0-9\\s]", 99) }
    val SERVICE_GROUP_CODE_MASK_PATTERN by lazy { compile("[a-zA-Z]{1,7}-?[0-9]{0,8}-?[a-zA-Z]{0,5}") }

    val VENDOR_INVITE_CODE_PATTERN by lazy { compile("[0-9]{8,14}-[a-zA-Z]{20}") }
    val VENDOR_INVITE_CODE_MASK_PATTERN by lazy { compile("[0-9]{0,14}-?[a-zA-Z]{0,20}") }

    val WAREHOUSE_NAME_PATTERN by lazy { compile("[A-Z][a-zA-Z0-9\\W_]{0,49}") } // 1-50 symbols
    val WAREHOUSE_CODE_PATTERN by lazy { compile("[a-zA-Z0-9]{1,10}") } // 1-50 symbols
    val DATE_PATTERN by lazy { compile("-?[0-9]{1,5}") } // date in days
    val DATE_TIME_PATTERN by lazy { compile("-?[0-9]{1,13}") } // date time in milliseconds
    val EMAIL_PATTERN by lazy { compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    ) }
    val EMAIL_PATTERN_MASK by lazy { compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-]{1,256}\\@?[a-zA-Z0-9]?[a-zA-Z0-9\\-]{0,64}(\\.?[a-zA-Z0-9]?[a-zA-Z0-9\\-]{0,25})+"
    ) }

    val PHONE_PATTERN by lazy { compile("\\+[0-9]{6,20}") }
    val PHONE_PATTERN_MASK by lazy { compile("\\+[0-9]{0,20}") }
    val BUSINESS_NAME_PATTERN by lazy { buildPatternSet("\"0-9'\\-,.:;\\s*\\_\\&", 99) }
    val BUSINESS_NAME_MASK_PATTERN by lazy { buildPatternSet("0-9'\\-,.:;\\s*\\_\\&", 1, 100) }
    val ADDRESS_PATTERN by lazy { buildPatternSet("0-9\\-,.:;\\s()/\\\\*", 1000) }
    val FAX_PATTERN by lazy { compile("[0-9]{0,11}") }
    val REGIONS_PATTERN by lazy { buildPatternSet("'\\-\\s,0-9", 100) } // compile("[A-Za-z'\\-\\s,0-9]{0,100}") }
    val CITIES_PATTERN by lazy { buildPatternSet("'\\-\\s,", 100) }
    val STREET_PATTERN by lazy { buildPatternSet("0-9\\-\\s',", 50) }
    val BUILDING_PATTERN by lazy { compile("[0-9\\-/\\\\]{1,50}") }
    val INDEX_PATTERN by lazy { compile("[0-9\\-]{3,11}") }
    val NOTE_PATTERN by lazy { buildPatternSet(NOTE_GENERIC_PATTER, 10000) }
    val CODE_3_PATTERN by lazy { compile("[0-9]{3}") }
    val TIME_TO_PREPARE_PATTERN by lazy { compile("[0-9\\-]{1,10}") }
    val INSTRUCTIONS_FOR_RETURN_PATTERN by lazy { buildPatternSet("0-9\\W_", 10000) }
    val DELIVERY_POINT_NAME_PATTERN by lazy {
        buildPatternSet(
            "0-9@#№\$%^&*()\\_=/|{}<>\\-\\.\\,\\:\\;\\u0020",
            99
        )
    } // 1-100 symbols

    val BANK_NAME_PATTERN by lazy { buildPatternSet("0-9\\s\\-\\\\/'.,()№#\$%*|_", 5, 500) }
    val BANK_ADDRESS_PATTERN by lazy { buildPatternSet("0-9\\s\\-\\\\/'.,()№#\$%*|_", 5, 500) }
    val BANK_ACCOUNT_PATTERN by lazy { compile("[A-Z]{2}\\d{2}[A-Z\\d]{4}[A-Z\\d]{5,34}") }
    val BANK_ACCOUNT_PATTERN_LIGHT by lazy { compile("[a-zA-Z0-9\\-\\.\\*]{5,50}") }
    val BANK_ACCOUNT_PATTERN_LIGHT_MASK by lazy { compile("[a-zA-Z0-9\\-\\.\\*]{1,50}") }
    val BANK_ACCOUNT_NAME_PATTERN by lazy { buildPatternSet("0-9\\s\\-,.\\\\/'\"()№#$%*|_:;", 1, 100) }
    val CVV_PATTERN by lazy { compile("[0-9]{3}") }
    val VALIDITY_PERIOD_PATTERN by lazy { compile("[0-9]{4}") }

    //val SWIFT_PATTERN by lazy { compile("[A-Z\\d]{4,5}[\\s\\-][A-Z]{2}[\\s\\-][A-Z\\d]{2}[\\s\\-][A-Z\\d]{3}") }
    val SWIFT_PATTERN by lazy { compile("[A-Z\\d]{4,5}[A-Z]{2}[A-Z\\d]{2}[A-Z\\d]{3}") }
    val BIC_PATTERN by lazy { compile("[A-Z\\d]{4,5}") }

    val ORDER_COMMENT_PATTERN by lazy { buildPatternSet(NOTE_GENERIC_PATTER, 10000) }
    val TERM_DATE_PATTERN by lazy { compile("[0-9\\-]{1,13}") }
    val ORDER_DATE_PATTERN by lazy { compile("[0-9\\-]{1,13}") }
    val ORDER_CONTACT_NAME_PATTERN by lazy { buildPatternSet("\\-\\u0020", 50) }
    val ORDER_CONTACT_LASTNAME_PATTERN by lazy { buildPatternSet("\\-\\u0020", 50) }
    val ORDER_NUM_PATTERN by lazy { compile("[0-9A-Za-z\\-]{1,14}") }
    val ORDER_PAYMENT_SYSTEM_CODE by lazy { compile("[a-zA-Z0-9\\_]{3,20}") }

    val PAYMENT_NUM_PATTERN by lazy { compile("[0-9A-Za-z\\-]{1,30}") }

    val DISPUTE_DESCRIPTION_PATTERN by lazy { buildPatternSet("0-9`'\\.\\,\\:\\;\\-\\=\\u0009\\u0020\\n\\u000D\\u000A", 5, 10000) }
    val DISPUTE_ANSWER_PATTERN by lazy { buildPatternSet("0-9\\.\\,\\:\\;\\-\\=\\u0020", 5, 10000) }
    val DISPUTE_CODE_PATTERN by lazy { compile("[0-9]{4}-[A-z]{4}") }
    val DISPUTE_CODE_MASK_PATTERN by lazy { compile("[0-9]{1,4}-?[A-z]{0,4}") }

    val REVIEW_PRODUCT_COMMENT_PATTERN by lazy { buildPatternSet("0-9`'\\.\\,\\:\\;\\-\\=\\u0020", 5, 10000) }
    val REVIEW_PRODUCT_RATING_PATTERN by lazy { compile("[1-5]{1}") }
    val REVIEW_PRODUCT_CODE_PATTERN by lazy { compile("[0-9]{3,4}\\-[a-zA-Z]{8}") }

    //Catalog===========================================================================================================
    val UNIT_CODE_PATTERN by lazy { compile("[a-zA-Z0-3⋅°/()Ω\"\\%\\-\\u0020]{1,50}") }
    val UNIT_NAME_PATTERN by lazy { compile("[a-zA-Z0-3()\\-\\u0020]{1,50}") }
    val CODE_MASK_PATTERN by lazy { compile("[0-9]{1,20}") }

    val NAME_MANUFACTURER_MASK_PATTERN by lazy { compile("[a-zA-Z0-9\\u0020]{1,50}") }
    val NAME_MANUFACTURER_PATTERN by lazy { compile("[A-Z][a-zA-Z0-9\\u0020]{0,49}") }

    val NUM_PRIORITY_PATTERN by lazy { compile("[1-3]") }
    val NAME_PARAM_GROUP_MASK_PATTERN by lazy { compile("[a-zA-Z\\u0020]{1,50}") }
    val NAME_PARAM_GROUP_PATTERN by lazy { compile("[A-Z][a-zA-Z\\u0020]{0,49}") }

    val NUM_PRIORITY_ATTRIBUTE_PATTERN by lazy { compile("[1-9]|[12][0-9]|30") }
    val NAME_ATTRIBUTE_MASK_PATTERN by lazy { compile("[a-zA-Z0-9/|&()<>\\.\\,\\:\\;\\u0020]{1,50}") }
    val NAME_ATTRIBUTE_PATTERN by lazy { compile("[A-Z][a-zA-Z0-9/|&()<>\\.\\,\\:\\;\\u0020]{0,49}") }

    val NAME_ABSTRACT_PRODUCT_MASK_PATTERN by lazy { compile("[a-zA-Z0-9\\u0020]{1,50}") }
    val NAME_ABSTRACT_PRODUCT_PATTERN by lazy { compile("[A-Z][a-zA-Z0-9\\u0020]{0,49}") }

    val VALUE_ATTRIBUTE_PATTERN by lazy { buildPatternSet("0-9@#№$%^&=/|{}<>()⋅\\_\\u0020\\.\\,\\:\\;\\-\"²",1 ,500) }

    val NAME_PRODUCT_MASK_PATTERN by lazy { buildPatternSet("[a-zA-Z0-9@#№$%^&*()\\_=/|{}<>\\-\\.\\,\\:\\;\\u0020]", 100) }
    val NAME_PRODUCT_PATTERN by lazy { buildPatternSet("[a-zA-Z0-9@#№$%^&*()\\_=/|{}<>\\-\\.\\,\\:\\;\\u0020]", 5, 99) }

    val DESCRIPTION_PRODUCT_PATTERN by lazy { buildPatternSet("[a-zA-Z0-9@#№$%^&*()\\_=/|{}<>\\-\\.\\,\\:\\;\\u0009\\u0020\\n\\u000D\\u000A]", 10000) }

    val CATEGORY_CODE_PATTERN by lazy { compile("[A-Z0-9\\_]{1,50}") }
    val CATEGORY_MASK_PATTERN by lazy { compile("[A-Za-z0-9\\_\\u0020]{1,50}") }

    val STATUS_PATTERN by lazy { compile("[A-Z\\_]{1,50}") }

    //==================================================================================================================
    val LOCALE_PATTERN by lazy { compile("[a-z]{2}\\_[A-Z]{2}") }

    //Banking===========================================================================================================
    val KIND_CODE_PATTERN by lazy { compile("[A-Z\\_]{1,50}") }
    val STATUS_NAME_PATTERN by lazy { compile("[A-Za-z\\u0020]{1,50}") }
    val STATUS_CODE_PATTERN by lazy { compile("[A-Z_]{1,50}") }
    val NUM_ACCOUNT_TRANSACTION_PATTERN by lazy { compile("[0-9]{5,10}\\-[A-Za-z]{10}\\-[0-9]{5,10}") }
    val NUM_CARD_TRANSACTION_PATTERN by lazy { compile("[A-Za-z]{10}\\-[0-9]{5,10}\\-[A-Za-z]{15}") }
    val BPC_NUM_PATTERN by lazy { compile("[0-9]{16}") }
    val BPC_CVV_PATTERN by lazy { compile("[0-9]{3}") }
    val BPC_MONTH_PATTERN by lazy { compile("[0-1]{1}[0-9]{1}") }
    val BPC_YEAR_PATTERN by lazy { compile("[0-9]{2}") }
    val FIRST_NAME_CARD_PATTERN by lazy { compile("[A-Z\\u0020\\-]{1,40}") }
    val LAST_NAME_CARD_PATTERN by lazy { compile("[A-Z\\u0020\\-]{1,40}") }

    //Quiz =============================================================================================================
    val QUIZ_TEMPLATE_CODE by lazy { compile("[A-Za-z0-9\\_]{1,100}") }
    val QUIZ_TEMPLATE_GROUP by lazy { compile("[A-Z0-9\\_]{1,100}") }
    val QUIZ_TEMPLATE_NAME by lazy { compile("[A-Za-z0-9\\,\\.\\u0020]{1,200}") }
    val QUIZ_QUESTION_BODY by lazy { compile("[A-Za-z0-9\\,\\.\\?\\[\\]\\(\\)\\:\\;\\u0020]{1,10000}") }
    val QUIZ_REQUEST_CODE by lazy { compile("[a-z0-9]{5,19}-[A-Za-z0-9\\_]{1,100}-[0-9]{1,10}[A-za-z0-9]{10}") }
    val QUIZ_RESPONSE by lazy { compile("[A-Za-z0-9\\,\\.\\?\\[\\]\\(\\)\\:\\;\\u0020]{1,10000}") }
    val QUIZ_QUESTION by lazy { compile("[A-Za-z0-9\\,\\.\\?\\[\\]\\(\\)\\:\\;\\u0020]{1,10000}") }

    //LMS===============================================================================================================
    val LMS_REQUEST_CODE by lazy { compile("[0-9]{5}-[A-Za-z0-9\\_]{27}-[0-9]{1,100000}-[0-9]{1,100000}") }

    // referral Program
    val REFERRAL_PROGRAM_PATTERN by lazy { compile("RC[0-9]{1,20}") }
    val REFERRAL_PROGRAM_NOTE_PATTERN by lazy { buildPatternSet(NOTE_GENERIC_PATTER, 400) }

    // SandBox
    val INVITE_CODE by lazy { compile("[A-Za-z0-9\\_]{50}") }

    const val V4_CELL = "[0-9]{1,3}"
    val V4_PATTERN by lazy { compile("($V4_CELL.){3}$V4_CELL") }

    const val V6 = "a-f0-9"
    const val V6_EXT = "^$V6\\:"
    const val V6_CELL = "[$V6]{1,4}"
    val V6_PATTERN by lazy { compile("($V6_CELL:){7}$V6_CELL") }

    private fun buildPatternSet(basePattern: String, size: Int): Set<Pattern> = buildPatternSet(basePattern, 0, size)

    private fun buildPatternSet(basePattern: String, minSize: Int, maxSize: Int): Set<Pattern> = let {
        val length = "{$minSize,${maxSize - 1}}"
        setOf(
            compile("[$LATIN_CHARS$basePattern]$length"),
            compile("[$CYRILLIC_CHARS$basePattern]$length"),
            compile("^[$TURKISH_CHARS$basePattern]$length"),
            compile("[$ARABIC_CHARS$basePattern]$length")
        ).also {
            //println("################# build pattern: $it")
        }
    }
}
