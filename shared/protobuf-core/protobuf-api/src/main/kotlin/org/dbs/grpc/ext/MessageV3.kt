package org.dbs.grpc.ext

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Message

object MessageV3 {
    fun GeneratedMessage.isNotNull() = (this.serializedSize > 0)
    fun GeneratedMessage.isNull() = (this.serializedSize <= 0)
    fun Message.isNotNull() = (this.serializedSize > 0)
    fun Message.isNull() = (this.serializedSize <= 0)
}
