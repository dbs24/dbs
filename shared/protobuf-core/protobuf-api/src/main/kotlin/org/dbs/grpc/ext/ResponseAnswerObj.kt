package org.dbs.grpc.ext

import com.google.protobuf.Message
import kotlinx.coroutines.Job
import org.dbs.protobuf.core.ResponseAnswer

object ResponseAnswerObj {
    fun ResponseAnswer.Builder.hasErrors(): Boolean = (errorMessagesCount > 0)
    fun ResponseAnswer.Builder.noErrors(): Boolean = (errorMessagesCount == 0)
    fun ResponseAnswer.hasErrors(): Boolean = (errorMessagesCount > 0)
    fun ResponseAnswer.noErrors(): Boolean = (errorMessagesCount == 0)
    inline fun <reified T : Message> ResponseAnswer.unpackResponseEntity(): T = responseEntity.unpack(T::class.java)

    suspend fun ResponseAnswer.Builder.joins(jobs: Collection<Job>): Boolean = jobs.takeIf { noErrors() }
        ?.let {
            it.forEach { it.join() }
            noErrors()
        }
        ?: false

}
