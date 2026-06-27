package org.dbs.tree.user

import io.kotest.common.KotestInternal
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.tree.BaseCacheSpec
import org.dbs.tree.dao.UserDao
import org.dbs.tree.model.user.User
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime.now

@OptIn(KotestInternal::class)
@Suppress("unused")
class UserCacheTests : BaseCacheSpec(cacheName = "users") {

    @Autowired lateinit var userDao: UserDao

    init {
        "Verify user caching and evicting" {
            val login = "cacheUser"
            val now = now()
            val cachedUser = User(
                login = login,
                firstName = null, middleName = null, lastName = null,
                email = null, phone = null, password = null, birthDate = null,
                entityStatus = ES_USER_ANONYMOUS, createDate = now, modifyDate = now, closeDate = null,
            )

            val cache = getTestingCache()
            cache?.get(login)?.get() shouldBe null

            // create user
            userDao.saveUser(cachedUser)

            // find in cache
            val storedUser = userDao.findUserByLogin(login)
            requireNotNull(storedUser) { "Cached user not found ($login)" }
            storedUser.userId shouldNotBe null

            // verify redis cache via base class helpers
            verifyKeyInRedis(login)
            verifyCacheManagerType()

            val cachedObject = getTestingCache()?.get(login)?.get() as? User
            cachedObject shouldNotBe null
            cachedObject?.login shouldBe login

            // reset cache
            userDao.saveUser(storedUser.copy(firstName = "cacheUserFirstName"))

            val nullObject = getTestingCache()?.get(login)?.get() as? User
            nullObject shouldBe null
        }
    }
}

