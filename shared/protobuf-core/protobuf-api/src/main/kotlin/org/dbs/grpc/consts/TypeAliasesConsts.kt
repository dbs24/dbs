package org.dbs.grpc.consts


import com.google.protobuf.GeneratedMessage
import kotlinx.coroutines.flow.Flow
import org.dbs.protobuf.core.MainResponse

typealias GM = GeneratedMessage
typealias GMBuilder<T> = GeneratedMessage.Builder<T>
typealias ProtobufAny = com.google.protobuf.Any
typealias FlowResponse = Flow<MainResponse>
typealias FlowItemProcessor = (MainResponse) -> Unit
typealias RESP = MainResponse
