package org.dbs.entity.core.v2.consts

import org.dbs.entity.core.EntityStatusEnum

typealias ActionCodeId = Int
typealias EntityTypeId = Int
typealias EntityTypeName = String
typealias EntityKindId = Int
typealias ActionName = String
typealias ClosedEntity = (EntityStatusEnum) -> Boolean

object EntityV2Consts {

    object GenericStatuses {
        const val EGS_ACTUAL = 1
        const val EGS_CLOSED = 2
        const val EGS_APPROVED = 3
        const val EGS_CONFIRMED = 4
        const val EGS__DELIVERED = 5
        const val EGS_RETURN = 6
        const val EGS_OPENED = 7
        const val EGS_IN_MODERATION = 9
        const val EGS_FINISHED = 11
        const val EGS_DELETED = 12
        const val EGS_CANCELLED = 15
        const val EGS_COMPLETED = 16
        const val EGS_BANNED = 17
        const val EGS_DENIED = 18
        const val EGS_IN_PROGRESS = 20
        const val EGS_MODIFIED = 21
        const val EGS_REJECTED = 30
        const val EGS__TIMEOUT = 31
        const val EGS_SENT = 40
        const val EGS_AWAITING = 41
        const val EGS_EXPIRED = 42
        const val EGS_PAID = 43
        const val EGS_CREATED = 44
        const val EGS_DRAFT = 45
        const val EGS_ANONYMOUS = 50
        const val EGS_PLAY = 55
        const val EGS_OVERDUE = 60
        const val EGS_ACCEPTED = 65
    }


    object GenericAction {
        const val EGAS_CREATE_OR_UPDATE = 1
        const val EGAS_CREATE = 2
        const val EGAS_UPDATE = 3
        const val EGAS_UPDATE_STATUS = 4
        const val EGAS_UPDATE_PASSWORD = 4

    }

    const val ET_RATE = 100
    const val EA_RATE = 100
    const val CC_RATE = 100

    object CacheKeyId {
        const val ENTITY_ID = 1
        const val CC_ID = 10
        const val CC_CODE = 20
    }


}
