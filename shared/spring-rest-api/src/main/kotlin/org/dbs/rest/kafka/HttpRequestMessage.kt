package org.dbs.rest.kafka

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.kafka.api.KafkaDocument
import java.net.InetSocketAddress
import java.net.URI
import java.time.LocalDateTime


data class HttpRequestMessage(
    var requestHashCode: Int = 0,
    var method: String? = STRING_NULL,
    var path: String? = STRING_NULL,
    var bodyClassString: String? = STRING_NULL,
    var appId: Int? = null,
    var inetSocketAddress: InetSocketAddress? = null,
    var requestDate: LocalDateTime? = null,
    var finishDate: LocalDateTime? = null,
    var uri: URI? = null,
    var queryHeaders: String? = STRING_NULL,
    var queryParams: String? = STRING_NULL,
    var body: Any? = null,
    var response: Any? = null,
    var error: String? = STRING_NULL,
    @JsonIgnore
    var ready: Boolean = true,

    @JsonIgnore
    var completed: Boolean = false
) : KafkaDocument
