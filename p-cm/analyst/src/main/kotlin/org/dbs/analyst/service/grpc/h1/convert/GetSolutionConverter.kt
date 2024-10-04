package org.dbs.analyst.service.grpc.h1.convert

import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import org.dbs.cm.client.CreatedSolution as H2E
import org.dbs.cm.client.GetSolutionRequest as H2IN
import org.dbs.cm.client.GetSolutionResponse as H2OUT
import org.dbs.player.dto.solution.GetSolutionResponse as H1OUT
import org.dbs.player.dto.solution.SolutionDto as H1E
import org.dbs.player.dto.solution.SolutionDtoReq as H1IN

@JvmInline
value class GetSolutionConverter(override val entClass: Class<H2E>) : H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E> {
    override fun buildEntityH1(h2: H2E): H1E = h2.run {
        H1E(fen, status, depth, timeout)
    }
    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN.newBuilder()
            .setFen(fen.replace("_", " "))
            .setDepth(depth.toInt())
            .setTimeout(timeout.toInt())
            .build()
    }

    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
