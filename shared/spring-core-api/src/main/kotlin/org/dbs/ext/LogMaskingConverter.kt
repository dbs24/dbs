package org.dbs.ext

import org.apache.logging.log4j.core.LogEvent
//import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter
import java.util.regex.Matcher
import java.util.regex.Pattern


//@Plugin(name="org.dbs.ext.LogMaskingConverter", category = "Converter")
@ConverterKeys("koore","trscId")
class LogMaskingConverter(val name1: String, val style: String) : LogEventPatternConverter(name1, style) {

    override fun format(event: LogEvent, outputMessage: StringBuilder) {
        val message: String = event.getMessage().getFormattedMessage()
        var maskedMessage = message
        try {
            maskedMessage = mask(message)
        } catch (e: Exception) {
//            System.out.println(
//                TODO("Cannot convert element"),
//                TODO("Cannot convert element"),
//                Failed,
//                TODO("Cannot convert element"),
//                While,
//                TODO("Cannot convert element"),
//                Masking,
//                TODO("Cannot convert element")
//            )
            maskedMessage = message
        }
        outputMessage.append(maskedMessage)
    }


    private fun mask(message: String): String {
        var message: String = message
        var matcher: Matcher? = null
        val buffer = StringBuffer()
        matcher = CARD_NUMBER_PATTERN.matcher(message)
        maskMatcher(matcher, buffer, CARD_NUMBER_REPLACEMENT_REGEX)
        message = buffer.toString()
        buffer.setLength(0)
        return message
    }


    private fun maskMatcher(
        matcher: Matcher, buffer: StringBuffer, maskStr: String
    ): StringBuffer {
        while (matcher.find()) {
            matcher.appendReplacement(buffer, maskStr)
        }
        matcher.appendTail(buffer)
        return buffer
    }

    companion object {
        private val CARD_NUMBER_REGEX: String = "([0-9]{16})"
        private val CARD_NUMBER_PATTERN: Pattern = Pattern.compile(CARD_NUMBER_REGEX)
        private val CARD_NUMBER_REPLACEMENT_REGEX: String = "XXXXXXXXXXXXXXXX"
//        fun newInstance(options: Array<String?>?): org.dbs.ext.LogMaskingConverter {
//            return org.dbs.ext.LogMaskingConverter(
//                TODO("Cannot convert element"),
//                TODO("Cannot convert element"),
//                koore,
//                TODO("Cannot convert element"),
//                Thread.currentThread().name
//            )
//        }
    }
}
