package org.dbs.sandbox.repo.invite

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Service
import java.io.Serializable

//=========================================================
//@JvmInline
class TestValueClass<T>(private val someValue: BoxImpl<T>) : Box<T> by someValue {
    fun someFun(): Box<T> = someValue
    constructor(initValue: T) : this(BoxImpl(initValue))
}

//=========================================================
sealed interface Box<T>
class BoxImpl<T>(val t: T) : Box<T>

//=========================================================
abstract class AbstractEntity(
    @Transient
    override val entityId: Long
) : Entity {
    @delegate:Transient
    @get:Transient
    val valueClassInstance: TestValueClass<String> by lazy {
        TestValueClass("someValue")
    }
}

//=========================================================
sealed interface Entity : Serializable {
    val entityId: Long
}

//=========================================================
@Table("table_entity")
data class FinalEntity(
    @Id
    val inviteId: Long,
    val inviteCode: String,
) : AbstractEntity(inviteId)

//=========================================================
interface FailedRepo : CoroutineCrudRepository<FinalEntity, Long> {
    suspend fun findByInviteCode(inviteCode: String): FinalEntity?
}

//=========================================================
@Service
class InviteService4Test(val failedRepo: FailedRepo) {

    suspend fun doSomething() {
        failedRepo.findByInviteCode("someCode")?.valueClassInstance?.someFun()
    }
}