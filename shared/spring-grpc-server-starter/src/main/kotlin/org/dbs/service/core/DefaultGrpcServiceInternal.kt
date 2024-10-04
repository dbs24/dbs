package org.dbs.service.core

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["config.grpc.enable-outer-interceptor"])
class DefaultGrpcServiceInternal : AbstractGrpcServiceInternal()
