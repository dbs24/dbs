package org.dbs.analyst.service

import org.dbs.analyst.dao.SolutionDao
import org.dbs.kafka.consts.KafkaConsts
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_UCI_SOLUTION
import org.dbs.player.kafka.UciSolution
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
@Lazy(false)
class UciService(
    val solutionDao: SolutionDao,
) : AbstractRestApplicationService() {

    @KafkaListener(id = KAFKA_UCI_SOLUTION, groupId = KafkaConsts.Groups.CM_GROUP_ID, topics = [KAFKA_UCI_SOLUTION])
    fun receiveActorUsers(uciSolutions: Collection<UciSolution>) {
        logger.debug("receive solutions: [$uciSolutions]")
        //actorUserDao.saveActorUser(tasks)
    }

}
