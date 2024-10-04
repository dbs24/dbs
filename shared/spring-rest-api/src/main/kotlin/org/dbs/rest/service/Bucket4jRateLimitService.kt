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
    private val capacity: Long,
    @Value("\${$BUCKET_4J_RATE_LIMIT_TOKENS:$BUCKET_4J_RATE_LIMIT_TOKENS_DEF_VAL}")
    val tokens: Long,
    @Value("\${$BUCKET_4J_RATE_LIMIT_MINUTES:$BUCKET_4J_RATE_LIMIT_MINUTES_DEF_VAL}")
    val minutes: Long,
    @Value("\${$BUCKET_4J_RATE_LIMIT_TMP_MINUTES:$BUCKET_4J_RATE_LIMIT_MINUTES_BLACK_LIST_DEF_VAL}")
    val tmpMinutes: Long,
) : AbstractApplicationService() {

    private class IpBlackRecord(
        val ipMask: String,
        val invalidDate: OperDate,
    ) {
        override fun toString(): String = " $ipMask (${invalidDate.toString2()})"
    }

    private val ipTemporaryBlackList by lazy { createCollection<IpBlackRecord>() }

    //==================================================================================================================
    private val warnCapacity: Long = capacity / 2

    private val buckets = createMap<IpAddress, Bucket>().also {
        logger.debug { "trustedIps: $trustedIps" }
        logger.debug { "blackListIps: $blackListIps" }
    }

    private val whiteIpOnlyMode by lazy {
        onlyAllowedIp.isNotEmpty() && (onlyAllowedIp.all { it != BUCKET_4J_ONLY_ALLOWED_IPS_DEF_VAL })
    }

    override fun initialize() = super.initialize().also {
        if (whiteIpOnlyMode) {
            val warnMsg = "######## whiteIpOnlyMode activated: $onlyAllowedIp "
            logger.info { BANNER_ROW_BOLD_DELIMITER }
            logger.info { "$warnMsg" }
            logger.info { BANNER_ROW_BOLD_DELIMITER }
        }
    }

    fun validateRateLimit(ip: IpAddress) : Boolean =
        // whiteIpOnly mode
        if (whiteIpOnlyMode) {
            onlyAllowedIp.any { ip.start(it) }.also {
                if (!it) {
                    logger.error { "$ip not allowed by the whiteIpOnlyMode $onlyAllowedIp" }
                }
            }
        } else
        // blackListIps mode
            (blackListIps.any { ip.start(it) }).let {
                val now = now()
                if (it) {
                    false.also { logger.error { "$ip is in blacklist " } }
                } else {
                    (ipTemporaryBlackList.any { ip.start(it.ipMask) && (it.invalidDate > now) }).let {
                        if (it) {
                            false.also {
                                val tillDate =
                                    ipTemporaryBlackList.firstOrNull { ip.start(it.ipMask) }?.invalidDate ?: now
                                logger.error {
                                    "put ${ip.validIpV4()} in temporary blacklist till ${tillDate.toString2()} (remain ${
                                        now.d1d2Diff(
                                            tillDate
                                        )
                                    }) "
                                }
                            }
                        } else
                            trustedIps.any { ip.start(it) }.takeIf {
                                it.also {
                                    if (it) {
                                        logger.debug { "passed as trusted ip: $ip" }
                                    }
                                }
                            } ?: let {
                                buckets[ip]?.run {
                                    if (this.availableTokens < warnCapacity)
                                        logger.warn { "$ip: available tokens: ${this.availableTokens}|${capacity}" }

                                    this.tryConsume(1).also {
                                        if (!it) {
                                            logger.error { "$ip: access limit exceeded (capacity:$capacity, tokens:$tokens, minutes:$minutes)" }
                                        }
                                    }
                                } ?: addNewBucket(ip)
                            }
                    }
                }
            }

    private fun addNewBucket(ip: IpAddress) =
        ip.run {
            buckets[this] = Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(tokens, ofMinutes(minutes))))
                .build()
            logger.info { "new rate bucket ($ip, ${buckets.size} bucket(s))" }
            buckets.isNotEmpty()
        }

    fun addNewTemporaryBlackList(ipAddressOrMask: IpAddress, tillDate: OperDate = now().plusMinutes(tmpMinutes)) =
        with (ipTemporaryBlackList) {
            logger.warn { "add subnet $ipAddressOrMask to temporary blackList(till ${tillDate.toString2()})" }
                removeIf { it.ipMask == ipAddressOrMask }
                add(IpBlackRecord(ipAddressOrMask, tillDate))
            now().apply {
                removeIf { it.invalidDate < this }
            }

            if (isNotEmpty()) {
                logger.warn { "ipTemporaryBlackList = $ipTemporaryBlackList" }
            }
        }
}
