package org.dbs.grpc.ext

import com.google.protobuf.NullValue
import org.dbs.protobuf.core.BoolNullable
import org.dbs.protobuf.core.DoubleNullable
import org.dbs.protobuf.core.Int32Nullable
import org.dbs.protobuf.core.Int64Nullable
import org.dbs.protobuf.core.StringNullable

object GrpcNull {

    fun String.grpcGetOrNull(): String? = this.takeIf { it.isNotEmpty() }

    fun Int.grpcGetOrNull(): Int? = this.takeIf { it != 0 }
    fun String.isGrpcNull() = this.isEmpty()
    fun String.isGrpcNotNull() = !this.isGrpcNull()

    fun String.grpcGetOrTen() : String = this.takeIf { it.isNotEmpty() } ?: "10"
    fun String.grpcGetOrOne() : String = this.takeIf { it.isNotEmpty() } ?: "1"

    fun StringNullable.grpcGetOrNull() : String? = if (hasData()) data else null
    fun BoolNullable.grpcGetOrNull() : Boolean? = if (hasData()) data else null
    fun DoubleNullable.grpcGetOrNull() : Double? = if (hasData()) data else null
    fun Int32Nullable.grpcGetOrNull() : Int? = if (hasData()) data else null
    fun Int64Nullable.grpcGetOrNull() : Long? = if (hasData()) data else null

    fun String?.toGrpcNullable() = run {
        val builder = StringNullable.newBuilder()
        this?.let { builder.setData(it) } ?: builder.setNull(NullValue.NULL_VALUE)
    }

    fun Boolean?.toGrpcNullable() = run {
        val builder = BoolNullable.newBuilder()
        this?.let { builder.setData(it) } ?: builder.setNull(NullValue.NULL_VALUE)
    }

    fun Int?.toGrpcNullable() = run {
        val builder = Int32Nullable.newBuilder()
        this?.let { builder.setData(it) } ?: builder.setNull(NullValue.NULL_VALUE)
    }

    fun Long?.toGrpcNullable() = run {
        val builder = Int64Nullable.newBuilder()
        this?.let { builder.setData(it) } ?: builder.setNull(NullValue.NULL_VALUE)
    }

    fun Double?.toGrpcNullable() = run {
        val builder = DoubleNullable.newBuilder()
        this?.let { builder.setData(it) } ?: builder.setNull(NullValue.NULL_VALUE)
    }
}
