package org.dbs.rest.security

import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SERVER_EVENT_LOOP_GROUPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SERVER_PORT
import org.dbs.consts.SpringCoreConst.WebClientConsts.UNLIMITED_BUFFER
import org.dbs.consts.SysConst.INTEGER_ZERO
import org.dbs.consts.SysConst.STRING_ZERO
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.stereotype.Service
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.LoopResources

@Service
class NettyInitializerService : AbstractApplicationService() {

    @Value("\${$CONFIG_SERVER_PORT:$STRING_ZERO}")
    val serverPort: Int = 0

    @Value("\${$CONFIG_SERVER_EVENT_LOOP_GROUPS:$STRING_ZERO}")
    val eventLoopGroups: Int = 0

    @Bean
    fun codecCustomizer(): CodecCustomizer = CodecCustomizer { it.defaultCodecs().maxInMemorySize(UNLIMITED_BUFFER) }

    @Bean
    fun reactorClientHttpConnector(r: ReactorResourceFactory): ReactorClientHttpConnector =
        ReactorClientHttpConnector(r) { m: HttpClient? -> m }

    @Bean
    fun nettyResourceFactory(eventLoopGroup: NioEventLoopGroup): ReactorResourceFactory =
        ReactorResourceFactory().also {
            it.loopResources = LoopResources { eventLoopGroup }
            it.isUseGlobalResources = false
        }

    @Bean
    fun nioEventLoopGroup() = if (eventLoopGroups == INTEGER_ZERO) {
        Runtime.getRuntime().availableProcessors() * 2 + 2
    } else {
        eventLoopGroups
    }.run {
        logger.info("AvailableProcessors/threads: ${Runtime.getRuntime().availableProcessors()}")
        logger.info("NioEventLoopGroup: $this eventLoopGroups")
        NioEventLoopGroup(this)
    }

    @DependsOn("nioEventLoopGroup")
    @Bean
    fun factory(eventLoopGroup: NioEventLoopGroup): NettyReactiveWebServerFactory =
        NettyReactiveWebServerFactory().also {

            eventLoopGroup.register(NioServerSocketChannel())

            it.addServerCustomizers({ httpServer ->
                httpServer.port(serverPort)
                httpServer.accessLog(true)
                logger.info(
                    "registry webServer factory (${it.javaClass.simpleName}, " +
                            "${httpServer.javaClass.simpleName}, port: $serverPort,  event loop groups: " +
                            "${eventLoopGroup.executorCount()})"
                )

                httpServer.runOn(eventLoopGroup)
            })
        }
}
