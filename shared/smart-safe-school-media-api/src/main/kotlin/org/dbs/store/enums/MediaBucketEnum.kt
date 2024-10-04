package org.dbs.store.enums

enum class MediaBucketEnum(val bucketName: String) {
    DEFAULT("junit.sss"),
    STORE_DEFAULT("store.default"),
    SCHOOL_DEFAULT("school.default")
    ;

    companion object {
        fun getBucket(bucketName: String): MediaBucketEnum = MediaBucketEnum.entries
            .find { it.bucketName == bucketName }
            ?: throw NoSuchElementException("Bucket with name [$bucketName] not found")
    }
}
