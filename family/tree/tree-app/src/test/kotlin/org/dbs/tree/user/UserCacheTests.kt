package org.dbs.tree.user

import io.kotest.common.KotestInternal
import io.kotest.core.annotation.Isolate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.test.ko.BaseSpec
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.tree.dao.UserDao
import org.dbs.tree.model.user.User
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Import
import java.time.LocalDateTime.now

@OptIn(KotestInternal::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class]
)
@Import(TreeConfig::class)
@Suppress("unused")
@Isolate
class UserCacheTests : BaseSpec(), Logging {

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var cacheManager: CacheManager

    override val source = "cache"

    init {
        beforeSpec {
            cacheManager.getCache("users")?.clear()
        }

        afterSpec {

        }

        beforeTest {
            //clearDatabase()
        }

        "Verify user caching and evicting" {

            val login = "cacheUser"
            val now = now()
            val cachedUser = User(
                login = login,
                firstName = null,
                middleName = null,
                lastName = null,
                email = null,
                phone = null,
                password = null,
                birthDate = null,
                entityStatus = ES_USER_ANONYMOUS,
                createDate = now,
                modifyDate = now,
                closeDate = null,
            )

            val cache = cacheManager.getCache("users")
            cache?.get(login)?.get() shouldBe null

            // create user
            userDao.saveUser(cachedUser)

            // find in cache
            val storedUser = userDao.findUserByLogin(login)

            requireNotNull(storedUser) {"Cached user not found ($login)"}

            storedUser.userId shouldNotBe null

            val cacheResult = cacheManager.getCache("users")

            val cachedObject = cacheResult?.get(login)?.get() as? User

            cachedObject shouldNotBe null
            cachedObject?.login shouldBe login

            // reset cache
            userDao.saveUser(storedUser.copy(
                firstName = "cacheUserFirstName",
            ))

            val evictedCache = cacheManager.getCache("users")
            val nullObject = evictedCache?.get(login)?.get() as? User

            nullObject shouldBe null

        }
    }
}
