package org.dbs.tree.project

import io.kotest.common.KotestInternal
import io.kotest.core.annotation.Isolate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.test.ko.BaseSpec
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.tree.dao.ProjectDao
import org.dbs.tree.dao.UserDao
import org.dbs.tree.model.project.Project
import org.dbs.tree.model.user.User
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_PROJECT_ACTUAL
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
class ProjectCacheTests : BaseSpec(), Logging {

    @Autowired
    lateinit var projectDao: ProjectDao

    @Autowired
    lateinit var cacheManager: CacheManager

    @Autowired
    lateinit var userDao: UserDao

    override val source = "cache"

    init {
        beforeSpec {
            cacheManager.getCache("projects")?.clear()
        }

        afterSpec {

        }

        beforeTest {
            //clearDatabase()
        }

        "Verify project caching and evicting" {

            val login = "cacheProjectUser"
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

            userDao.saveUser(cachedUser)

            val savedUser = userDao.findUserByLogin(login)

            requireNotNull(savedUser) {"Cached user not found ($login)"}

            val shortName = "cacheProject"
            val cachedProject = Project(
                shortName = shortName,
                fullName = null,
                ownerId = savedUser.userId!!,
                entityStatus = ES_PROJECT_ACTUAL,
                createDate = now,
                modifyDate = now,
                closeDate = null,
            )

            val cache = cacheManager.getCache("projects")
            cache?.get(shortName)?.get() shouldBe null

            // create user
            projectDao.saveProject(cachedProject)

            // find in cache
            val storedProject = projectDao.findProjectByShortName(shortName)

            requireNotNull(storedProject) {"Cached project not found ($shortName)"}

            storedProject.projectId shouldNotBe null

            val cacheResult = cacheManager.getCache("projects")

            val cachedObject = cacheResult?.get(shortName)?.get() as? Project

            cachedObject shouldNotBe null
            cachedObject?.shortName shouldBe shortName

            // reset cache
            projectDao.saveProject(storedProject.copy(
                fullName = "cacheProjectFullName",
            ))

            val evictedCache = cacheManager.getCache("projects")
            val nullObject = evictedCache?.get(shortName)?.get() as? Project

            nullObject shouldBe null

        }
    }
}
