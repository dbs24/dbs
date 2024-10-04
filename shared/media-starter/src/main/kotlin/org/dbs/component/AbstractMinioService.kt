package org.dbs.component

import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import io.minio.SetBucketPolicyArgs
import io.minio.UploadObjectArgs
import org.dbs.application.core.api.LateInitVal
import org.dbs.consts.RestHttpConsts.URI_HTTPS
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.springframework.core.io.InputStreamResource
import java.io.ByteArrayInputStream


abstract class AbstractMinioService : AbstractApplicationService() {
    private val minioClient = LateInitVal<MinioClient>()

    private val props by lazy { findCanonicalService(MediaProperties::class) }

    override fun initialize() = super.initialize().also {
        initMinioClient()
    }

    private fun initMinioClient() {
        logger.debug(
            "initialize minio client (${props.minioHost}, accessKey = ${props.accessKey}, secretKey = [protected])"
        )
        minioClient.init(MinioClient.builder()
            .endpoint(props.minioHost)
            .credentials(props.accessKey, props.secretKey)
            .build())

        addUrl4LivenessTracking(props.minioHost, javaClass.simpleName)

        if (!props.minioHost.startsWith(URI_HTTPS)) {
            logger.warn("SSL is off, disable CertCheck")
            minioClient.value.ignoreCertCheck()
        }

        logger.debug("listBuckets.count: ${minioClient.value.listBuckets().count()}")
    }

    fun uploadFile(fileName: String, filePath: String, bucketName: String) =
        minioClient.run {
            if (!minioClient.value.bucketExists(
                    BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
                )
            ) {
                //logger.debug("create new bucket {}/{}", minioHost, bucketName)
                minioClient.value.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
                minioClient.value.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(getJsonBucketPolicy(bucketName))
                        .build()
                )
            }

            //logger.debug("Upload '{}' to {}/{}", fileName, minioHost, bucketName)

            minioClient.value.uploadObject(
                UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(fileName)
                    .filename(filePath)
                    .build()
            )
            //logger.debug("success upload file '{}' to bucket {}/{}", fileName, minioHost, bucketName)

            // return URI image
            "/$bucketName/$fileName"
        }

    fun removeFile(fileName: String, bucketName: String) {
        minioClient.value.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .build()
        )
    }

    fun downloadFile(fileName: String, bucketName: String): InputStreamResource = run {
        val result = minioClient.value.getObject(
            GetObjectArgs.builder()
                .`object`(fileName)
                .bucket(bucketName)
                .build()
        )
        InputStreamResource(ByteArrayInputStream(result.readAllBytes()))
    }

    private fun getJsonBucketPolicy(bucketName: String) = """
        {
            "Statement": [
                {
                    "Action": ["s3:GetBucketLocation", "s3:ListBucket", "s3:GetBucketPolicy"],
                    "Effect": "Allow",
                    "Principal": { "AWS": ["*"] },
                    "Resource": "arn:aws:s3:::$bucketName",
                    "Sid": ""
                },
                 {
                     "Action": ["s3:GetObject"],
                     "Effect": "Allow",
                     "Principal": { "AWS": ["*"] },
                     "Resource": "arn:aws:s3:::$bucketName/*",
                     "Sid": ""
                 }
             ],
             "Version": "2012-10-17"
        }
    """.trimIndent()
}
