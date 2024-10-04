package org.dbs.component

import org.dbs.consts.SpringCoreConst.PropertiesNames.MINIO_ACCESS_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MINIO_HOST_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MINIO_SECRET_KEY
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class MediaProperties(

    @Value("\${$MINIO_HOST_KEY:https://media-dev.smartsafeschool.com:9000}")
    val minioHost: String,

    @Value("\${$MINIO_ACCESS_KEY:minio}")
    val accessKey: String,

    @Value("\${$MINIO_SECRET_KEY:minioPass2022}")
    val secretKey: String
)
