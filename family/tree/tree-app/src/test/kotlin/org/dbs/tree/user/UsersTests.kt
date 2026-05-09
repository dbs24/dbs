package org.dbs.tree.user

import io.grpc.ManagedChannel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.service.dao.EntityDao
import org.dbs.test.ko.BaseGrpcSpec
import org.dbs.tree.TreeApplication
import org.dbs.tree.client.UserServiceGrpcKt
import org.dbs.tree.config.TreeConfig
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.UserCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.UserCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.dbs.tree.client.CreateOrUpdateUserRequest as REQ

typealias Stub = UserServiceGrpcKt.UserServiceCoroutineStub

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [TreeApplication::class]
)
@Import(TreeConfig::class)
class UsersGrpcTests : BaseGrpcSpec() {

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var entityDao: EntityDao

    private lateinit var userStub: Stub

    override fun initStubs(channel: ManagedChannel) {
        userStub = Stub(channel)
    }

    init {

        "Success: Create user via gRPC network call" {
            val request = REQ.newBuilder()
                .setLogin("valid_user")
                .setEmail("test@example.com")
                .setPassword("Strong1Password")
                .build()

            userStub.createOrUpdateUser(request).apply {

                userLogin shouldBe "valid_user"
                email shouldBe "test@example.com"
                status shouldBe ES_USER_ANONYMOUS.entityStatusName
            }

            val user = userRepo.findByLogin("valid_user")
            user shouldNotBe null

            val userId: Long = user?.userId!!
            userId shouldNotBe null

            entityDao.requireActions(userId,EA_CREATE_OR_UPDATE_USER)
        }

        "Success: Create invalid user via gRPC network call" {
            val request = REQ.newBuilder()
                .setLogin("")
                .build()

            val errorList = getErrorsFromStub {
                userStub.createOrUpdateUser(request)
            }

            logger.info { " ##### errorList: $errorList" }

        }
    }
}
