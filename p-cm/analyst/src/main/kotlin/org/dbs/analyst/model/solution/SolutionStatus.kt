package org.dbs.analyst.model.solution

import org.dbs.player.enums.PlayerStatusEnum
import org.dbs.spring.ref.AbstractRefEntity
import org.dbs.player.enums.SolutionStatusEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("solution_statuses_ref")
data class SolutionStatus(
    @Id
    @Column("solution_status_id")
    val solutionStatusId: SolutionStatusEnum,
    @Column("solution_status_name")
    val solutionStatusName: String,
    ) : AbstractRefEntity<Int>() {
    override fun getId() = solutionStatusId.getCode()
}
