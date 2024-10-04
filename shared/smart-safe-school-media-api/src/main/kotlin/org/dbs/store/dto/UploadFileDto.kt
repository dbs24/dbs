package org.dbs.store.dto

import org.springframework.http.codec.multipart.FilePart

data class UploadFileDto(
    val bucketName: String,
    val filePart: FilePart,
    val previousFileName: String?
)
