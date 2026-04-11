package org.dbs.rest.service

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.d1d2Diff
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toString2
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.start
import org.dbs.consts.IpAddress
import org.dbs.consts.OperDate
import org.dbs.consts.SpringCoreConst.PropertiesNames.BANNER_ROW_BOLD_DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_BLACK_LIST_IPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_BLACK_LIST_IPS_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ONLY_ALLOWED_IPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ONLY_ALLOWED_IPS_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_CAPACITY
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_CAPACITY_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_MINUTES
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_MINUTES_BLACK_LIST_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_MINUTES_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_TMP_MINUTES
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_TOKENS
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_RATE_LIMIT_TOKENS_DEF_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_TRUSTED_IPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_TRUSTED_IPS_DEF_VAL
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.validIpV4
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.time.Duration.ofMinutes
import java.time.LocalDateTime.now

@Lazy(false)
@Service
@ConditionalOnProperty(name = [BUCKET_4J_ENABLED], havingValue = STRING_TRUE)
class Bucket4jRateLimitService(
    @Value("\${$BUCKET_4J_ONLY_ALLOWED_IPS:$BUCKET_4J_ONLY_ALLOWED_IPS_DEF_VAL}")
    val onlyAllowedIp: Collection<IpAddress>,
    @Value("\${$BUCKET_4J_TRUSTED_IPS:$BUCKET_4J_TRUSTED_IPS_DEF_VAL}")
    val trustedIps: Collection<IpAddress>,
    @Value("\${$BUCKET_4J_BLACK_LIST_IPS:$BUCKET_4J_BLACK_LIST_IPS_DEF_VAL}")
    val blackListIps: Collection<IpAddress>,
    @Value("\${$BUCKET_4J_RATE_LIMIT_CAPACITY:$BUCKET_4J_RATE_LIMIT_CAPACITY_DEF_VAL}")
    private val capacityValue: Long,
    @Value("\${$BUCKET_4J_RATE_LIMIT_TOKENS:$BUCKET_4J_RATE_LIMIT_TOKENS_DEF_VAL}")
    val tokensValue: Long,
    @Value("\${$BUCKET_4J_RATE_LIMIT_MINUTES:$BUCKET_4J_RATE_LIMIT_MINUTES_DEF_VAL}")
    val minutesValue: Long,
    @Value("\${$BUCKET_4J_RATE_LIMIT_TMP_MINUTES:$BUCKET_4J_RATE_LIMIT_MINUTES_BLACK_LIST_DEF_VAL}")
    val tmpMinutesValue: Long,
) : AbstractApplicationService() {

    private class IpBlackRecord(val ipMask: String, val invalidDate: OperDate) {
        override fun toString(): String = " $ipMask (${invalidDate.toString2()})"
    }

    private val ipTemporaryBlackList by lazy { createCollection<IpBlackRecord>() }
    private val capacity by lazy { capacityValue }
    private val tokens by lazy { tokensValue }
    private val minutes by lazy { minutesValue }
    private val tmpMinutes by lazy { tmpMinutesValue }
    private val warnCapacity by lazy { capacity / 2 }

    private val buckets by lazy {
        createMap<IpAddress, Bucket>().also {
            logger.debug { "trustedIps: $trustedIps" }
            logger.debug { "blackListIps: $blackListIps" }
        }
    }

    private val whiteIpOnlyMode by lazy {
        onlyAllowedIp.isNotEmpty() && onlyAllowedIp.none { it == BUCKET_4J_ONLY_ALLOWED_IPS_DEF_VAL }
    }

    override fun initialize() = super.initialize().also {
        whiteIpOnlyMode.takeIf { it }?.run {
            logger.info { BANNER_ROW_BOLD_DELIMITER }
            logger.info { "######## whiteIpOnlyMode activated: $onlyAllowedIp " }
            logger.info { BANNER_ROW_BOLD_DELIMITER }
        }
    }

    fun validateRateLimit(ip: IpAddress): Boolean = when {
        // whiteIpOnly mode
        whiteIpOnlyMode -> onlyAllowedIp.any { ip.start(it) }.also { allowed ->
            allowed.takeUnless { it }?.run { logger.error { "$ip not allowed by whiteIpOnlyMode $onlyAllowedIp" } }
        }

        // permanent blacklist
        blackListIps.any { ip.start(it) } -> false.also { logger.error { "$ip is in blacklist" } }

        // temporary blacklist
        else -> run {
            val now = now()
            ipTemporaryBlackList.firstOrNull { ip.start(it.ipMask) && it.invalidDate > now }?.let { record ->
                logger.error { "put ${ip.validIpV4()} in temporary blacklist till ${record.invalidDate.toString2()} (remain ${now.d1d2Diff(record.invalidDate)})" }
                false
            } ?: trustedIps.any { ip.start(it) }.let { isTrusted ->
                // trusted ip bypass
                if (isTrusted) {
                    logger.debug { "passed as trusted ip: $ip" }
                    true
                } else {
                    // bucket rate limiting
                    buckets[ip]?.let { bucket ->
                        bucket.availableTokens.takeIf { it < warnCapacity }?.run {
                            logger.warn { "$ip: available tokens: $this|$capacity" }
                        }
                        bucket.tryConsume(1).also { success ->
                            success.takeUnless { it }?.run {
                                logger.error { "$ip: access limit exceeded (capacity:$capacity, tokens:$tokens, minutes:$minutes)" }
                            }
                        }
                    } ?: addNewBucket(ip)
                }
            }
        }
    }

    private fun addNewBucket(ip: IpAddress): Boolean = ip.run {
        buckets[this] = Bucket.builder()
            .addLimit(Bandwidth.classic(capacity, Refill.greedy(tokens, ofMinutes(minutes))))
            .build()
        logger.info { "new rate bucket ($ip, ${buckets.size} bucket(s))" }
        true
    }

    fun addNewTemporaryBlackList(ipAddressOrMask: IpAddress, tillDate: OperDate = now().plusMinutes(tmpMinutes)) =
        ipTemporaryBlackList.run {
            logger.warn { "add subnet $ipAddressOrMask to temporary blackList(till ${tillDate.toString2()})" }
            removeIf { it.ipMask == ipAddressOrMask }
            add(IpBlackRecord(ipAddressOrMask, tillDate))
            now().let { currentTime -> removeIf { it.invalidDate < currentTime } }
            takeIf { it.isNotEmpty() }?.run { logger.warn { "ipTemporaryBlackList = $this" } }
        }
}
