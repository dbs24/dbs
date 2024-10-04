package org.dbs.tasker.service

import org.dbs.ext.CoroutineFuncs.processItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.kafka.consts.KafkaConsts.Groups.CM_GROUP_ID
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_UCI_TASKER
import org.dbs.kafka.service.KafkaUniversalService
import org.dbs.player.kafka.UciTask
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.dbs.tasker.uci.StockFishTask
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Lazy(false)
class TaskerService(
    val kafkaUniversalService: KafkaUniversalService
) : AbstractRestApplicationService() {

    private val jobs by lazy { createCollection<Job>() }

    private val channelTasks by lazy { Channel<Collection<UciTask>>(UNLIMITED) }

    @KafkaListener(id = KAFKA_UCI_TASKER, groupId = CM_GROUP_ID, topics = [KAFKA_UCI_TASKER])
    fun receiveActorUsers(tasks: Collection<UciTask>) {
        logger.debug("receive tasks: [$tasks]")
        //actorUserDao.saveActorUser(tasks)
        //channelTasks.addItems(tasks)

        runBlocking {
            tasks.forEach {
                jobs.add(launch {
                    Thread {
                        with(StockFishTask(it)) {
                            runTask()
                        }
                    }.start()
                })
            }
        }

        logger.debug("finish kafka listener")

    }
}
