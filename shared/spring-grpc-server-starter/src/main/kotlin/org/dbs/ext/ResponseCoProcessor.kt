package org.dbs.ext


import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineStart.LAZY
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.api.CommonJobs.JK_EMPTY_JOB
import org.dbs.api.JobKey
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.consts.GrpcConsts.Coroutines.HEAVY_SPEED_LIMIT_MS
import org.dbs.consts.GrpcConsts.Coroutines.MIN_SPEED_LIMIT
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.suspendNoArg
import org.dbs.ext.CoroutineFuncs.createSuperVisorScope
import org.dbs.grpc.consts.GM
import org.dbs.grpc.consts.GMBuilder
import org.dbs.grpc.ext.ResponseAnswerObj.hasErrors
import org.dbs.grpc.ext.ResponseAnswerObj.joins
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

@JvmInline
value class ResponseCoProcessorWrapper<T : GM, B : GMBuilder<B>>(private val responseCoProcessor: ResponseCoProcessor<T, B>) :
    ResponseCoProcessor<T, B> by responseCoProcessor

interface ResponseCoProcessor<T : GM, B : GMBuilder<B>> : Closeable, Logging {

    val jobsMap: MutableMap<JobKey, Job>
    val rab: RAB
    val coroutineScope: CoroutineScope

    private class IncompletedJobException(private val exceptionMessage: String) : RuntimeException(exceptionMessage)

    fun defaultCoroutineScope() = createSuperVisorScope()
    fun defaultJobsMap(): MutableMap<JobKey, Job> = createMap()

    fun isValidDto(): Boolean = true

    suspend fun finishResponse(): B

    private fun Set<JobKey>.key2job() =
        let { it.filter { it != JK_EMPTY_JOB }.map { jobsMap[it] ?: error("job '$it' not found in dependencyJob") } }

    /**
     * Launch Job
     *
     * @param jobKey [JobKey]
     *
     **/
    suspend fun launchJob(
        jobKey: JobKey,
        dependencyJobKey: Set<JobKey>,
        context: CoroutineContext = Dispatchers.Default.limitedParallelism(5),
        action: suspendNoArg
    ) {
        if (coroutineScope.isActive) {
            if (!rab.joins(dependencyJobKey.key2job())) {
                coroutineScope.cancel()
            } else {
                coroutineScope.launch(context + SecurityCoroutineContext(), LAZY) {
                    runCatching {
                        measureTimeMillis {
                            action()
                        }.also {
                            if (it <= MIN_SPEED_LIMIT) {
                                logger.warn("${jobKey}: the use of coroutine is not recommended for very speed code : took $it ms")
                            } else {
                                // for heavy operation
                                if (it > HEAVY_SPEED_LIMIT_MS) {
                                    yield()
                                }
                                logger.debug("${jobKey}: job execution time: took $it ms ${if (it > HEAVY_SPEED_LIMIT_MS) "[apply yield]" else EMPTY_STRING}")
                            }
                        }
                    }.getOrElse {
                        val msg = "$jobKey: register exception: $it"

                        when (it) {
                            is CancellationException -> {
                                logger.warn(msg)
                            }

                            else -> {
                                logger.error(msg)
                                rab.addErrorInfo(msg)
                                cancel(CancellationException(msg))
                                it.printStackTrace()
                            }
                        }
                        yield()
                    }
                }.also { co ->

                    co.invokeOnCompletion { th ->
                        th?.apply {
                            logger.warn("### $jobKey job was cancelled (${this})")
                            printStackTrace()
                        }
                        logger.trace("$jobKey: job was finished ")
                    }
                    co.start().also {
                        logger.apply {
                            if (it)
                                debug("$jobKey: job was started [${co}]")
                            else
                                error("$jobKey: job was not started")
                        }
                    }
                    jobsMap[jobKey]?.apply { error("job '$jobKey' already registered") }
                        ?: apply { jobsMap[jobKey] = co }
                }
            }
        } else {
            logger.warn { "$jobKey: coroutineScope is not active: ${coroutineScope.coroutineContext}" }
            yield()
        }
    }

    suspend fun launchJob(
        jobKey: JobKey,
        dependencyJob: JobKey = JK_EMPTY_JOB,
        context: CoroutineContext = Dispatchers.IO,
        action: suspendNoArg
    ) = launchJob(jobKey, setOf(dependencyJob), context, action)

    suspend fun launchJob(
        jobKey: JobKey,
        context: CoroutineContext = Dispatchers.IO,
        action: suspendNoArg
    ) = launchJob(jobKey, setOf(JK_EMPTY_JOB), context, action)

    suspend fun <B : GMBuilder<B>> finish(
        dependencyJobKeys: Set<JobKey>,
        builder: B,
        action: (B) -> B
    ): B = builder.takeUnless { coroutineScope.isActive and rab.noErrors() }
        ?: if (!rab.joins(dependencyJobKeys.key2job())) {
            coroutineScope.cancel()
            builder
        } else {
            action(builder)
        }

    fun finish(
        builder: B,
        action: (B) -> B
    ): B = builder.takeIf { rab.hasErrors() } ?: action(builder)

    suspend fun finish(
        dependencyJob: JobKey,
        builder: B,
        action: (B) -> B
    ): B = finish(setOf(dependencyJob), builder, action)

    fun flush() = jobsMap.apply {
        forEach {
            with(it.value) {
                if (!(isCompleted or !isActive)) error(IncompletedJobException("${it.key}: Job was not completed"))
            }
        }
        clear()
    }

    override fun close() {
        flush()
    }

    suspend fun execute(): B

}

suspend inline fun <T : GM, B : GMBuilder<B>> ResponseCoProcessor<T, B>.executeIternal(action: suspendNoArg): B = run {
    if (isValidDto()) {
        action()
    }
    finishResponse().also {
        flush()
    }
}

@JvmInline
value class SecurityCoroutineContext(
    private val securityContext: SecurityContext = SecurityContextHolder.getContext()
) : ThreadContextElement<SecurityContext?> {

    companion object Key : CoroutineContext.Key<SecurityCoroutineContext>

    override val key: CoroutineContext.Key<SecurityCoroutineContext> get() = Key
    override fun updateThreadContext(context: CoroutineContext): SecurityContext? = run {
        SecurityContextHolder.setContext(securityContext)
        SecurityContextHolder.getContext().takeIf { it.authentication?.let { true } ?: false }
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: SecurityContext?) =
        oldState?.let { SecurityContextHolder.setContext(oldState) } ?: SecurityContextHolder.clearContext()
}
