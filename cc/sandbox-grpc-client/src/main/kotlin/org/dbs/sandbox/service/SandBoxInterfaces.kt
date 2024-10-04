package org.dbs.sandbox.service

import org.dbs.grpc.consts.FlowItemProcessor


interface SandBoxInterfaces {

    fun subscribe2Invites(flowItemProcessor: FlowItemProcessor)
}
