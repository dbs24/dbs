package org.dbs.grpc.ext

import org.dbs.protobuf.core.QueryParam
import org.dbs.protobuf.core.QueryParamEnum
import org.dbs.protobuf.core.QueryParamsList

object QueryParamsList {

    fun QueryParamsList.getQueryParam(queryParamEnum: QueryParamEnum): String? = run {
        this.queryParamsListList.firstOrNull { it.queryParamEnum == queryParamEnum }?.queryParamValue
    }

    fun QueryParamsList.getQueryParamDef(queryParamEnum: QueryParamEnum, defaultValue: String): String =
        getQueryParam(queryParamEnum) ?: defaultValue

    fun createQueryParamsList(vararg queryParams: QueryParam): QueryParamsList = QueryParamsList.newBuilder().run {
        queryParams.forEach { addQueryParamsList(it) }
        build()
    }

    fun createQueryParamsList(qpAddAction: (QueryParamsList.Builder) -> Unit): QueryParamsList =
        QueryParamsList.newBuilder().run {
            qpAddAction(this)
            build()
        }

    fun createQueryParam(queryParam: QueryParamEnum, value: String): QueryParam = QueryParam.newBuilder().run {
        setQueryParamEnum(queryParam)
        setQueryParamValue(value)
        build()
    }
}
