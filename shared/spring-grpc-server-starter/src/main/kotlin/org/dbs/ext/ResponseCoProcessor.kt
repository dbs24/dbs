package org.dbs.ext


import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineStart.LAZY
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.api.CommonJobs.JK_EMPTY_JOB
import org.dbs.api.JobKey
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.consts.GrpcConsts.Coroutines.HEAVY_SPEED_LIMIT_MS
import org.dbs.consts.GrpcConsts.Coroutines.MIN_SPEED_LIMIT
import org.dbs.consts.GrpcConsts.YIELD_FALSE
import org.dbs.consts.suspendNoArg
import org.dbs.ext.CoroutineFuncs.createSuperVisorScope
import org.dbs.grpc.consts.GM
import org.dbs.grpc.consts.GMBuilder
import org.dbs.grpc.ext.ResponseAnswerObj.hasErrors
import org.dbs.grpc.ext.ResponseAnswerObj.joins
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

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
        doYieldAfterAction: Boolean = YIELD_FALSE,
        context: CoroutineContext = Dispatchers.Default.limitedParallelism(5),
        action: suspendNoArg
    ) {
        if (!coroutineScope.isActive) {
            logger.warn { "$jobKey: scope inactive: ${coroutineScope.coroutineContext}" }
            return yield()
        }

        // Fail early if dependencies aren't met
        if (!rab.joins(dependencyJobKey.key2job())) {
            coroutineScope.cancel("Dependencies failed for $jobKey")
            return
        }

        // Avoid double-registration before launching
        if (jobsMap.containsKey(jobKey)) {
            logger.error("Job '$jobKey' already registered")
            return
        }

        val job = coroutineScope.launch(context + SecurityCoroutineContext(), LAZY) {
            val start = System.currentTimeMillis()
            try {
                action()
                val duration = System.currentTimeMillis() - start

                handlePostAction(jobKey, duration, doYieldAfterAction)
            } catch (e: CancellationException) {
                logger.warn("$jobKey: cancelled: ${e.message}")
                throw e
            } catch (e: Throwable) {
                val msg = "$jobKey exception: ${e.message}"
                logger.error(msg, e)
                rab.addErrorInfo(msg)
                cancel(CancellationException(msg, e))
            } finally {
                yield()
            }
        }

        job.invokeOnCompletion { th ->
            if (th != null && th !is CancellationException) logger.error("$jobKey failed", th)
            logger.trace("$jobKey finished")
        }

        jobsMap[jobKey] = job
        if (job.start()) logger.debug("$jobKey started [$job]")
    }

    private suspend fun handlePostAction(key: JobKey, ms: Long, forceYield: Boolean) {
        when {
            ms <= MIN_SPEED_LIMIT -> logger.warn("$key: speed warning ($ms ms)")
            ms > HEAVY_SPEED_LIMIT_MS || forceYield -> {
                logger.debug("$key: execution $ms ms [yielded]")
                yield()
            }
            else -> logger.debug("$key: execution $ms ms")
        }
    }


    suspend fun launchJob(
        jobKey: JobKey,
        dependencyJob: JobKey = JK_EMPTY_JOB,
        doYieldAfterAction: Boolean = YIELD_FALSE,
        context: CoroutineContext = Dispatchers.IO,
        action: suspendNoArg
    ) = launchJob(jobKey, setOf(dependencyJob), doYieldAfterAction, context, action)

    suspend fun launchJob(
        jobKey: JobKey,
        doYieldAfterAction: Boolean = YIELD_FALSE,
        context: CoroutineContext = Dispatchers.IO,
        action: suspendNoArg
    ) = launchJob(jobKey, setOf(JK_EMPTY_JOB), doYieldAfterAction, context, action)

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
        SecurityContextHolder.getContext().takeIf { it.authentication?.let { true } == true }
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: SecurityContext?) =
        oldState?.let { SecurityContextHolder.setContext(oldState) } ?: SecurityContextHolder.clearContext()
}
