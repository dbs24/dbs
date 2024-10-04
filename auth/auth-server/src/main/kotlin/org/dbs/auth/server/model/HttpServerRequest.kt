package org.dbs.auth.server.model

import org.dbs.consts.SysConst.INTEGER_NULL
import org.dbs.consts.SysConst.LOCALDATETIME_NULL
import org.dbs.consts.SysConst.LONG_NULL
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tkn_requests")
class HttpServerRequest(
    @Id
    @Column("request_id")
    var requestId: Long? = LONG_NULL,

    @Column("request_method")
    var requestMethod: String? = STRING_NULL,

    @Column("request_path")
    var requestPath: String? = STRING_NULL,

    @Column("request_uri")
    var requestUri: String? = STRING_NULL,

    @Column("request_body")
    var requestBody: String? = STRING_NULL,

    @Column("response_body")
    var responseBody: String? = STRING_NULL,

    @Column("request_ip")
    var requestIp: String? = STRING_NULL,

    @Column("request_port")
    var requestPort: Int? = INTEGER_NULL,

    @Column("request_date")
    var requestDate: LocalDateTime? = LOCALDATETIME_NULL,

    @Column("application_id")
    var application: Int? = INTEGER_NULL,

    @Column("ban_id")
    var ipBan: Int? = INTEGER_NULL,

    @Column("query_headers")
    val queryHeaders: String? = STRING_NULL,

    @Column("query_params")
    val queryParams: String? = STRING_NULL,

    @Column("request_note")
    val requestNote: String? = STRING_NULL,

    @Column("request_error")
    var requestError: String? = STRING_NULL

) : AbstractRefEntity<Long>() {

    override fun getId() = requestId!!
}
