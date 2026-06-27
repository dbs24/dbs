package org.dbs.tree.project

import io.kotest.common.KotestInternal
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.tree.BaseCacheSpec
import org.dbs.tree.dao.ProjectDao
import org.dbs.tree.dao.UserDao
import org.dbs.tree.model.project.Project
import org.dbs.tree.model.user.User
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_PROJECT_ACTUAL
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime.now


@OptIn(KotestInternal::class)
@Suppress("unused")
class ProjectCacheTests : BaseCacheSpec(cacheName = "projects") {

    @Autowired lateinit var projectDao: ProjectDao
    @Autowired lateinit var userDao: UserDao

    init {
        "Verify project caching and evicting" {
            val login = "cacheProjectUser"
            val now = now()
            val cachedUser = User(
                login = login,
                firstName = null, middleName = null, lastName = null,
                email = null, phone = null, password = null, birthDate = null,
                entityStatus = ES_USER_ANONYMOUS, createDate = now, modifyDate = now, closeDate = null,
            )

            userDao.saveUser(cachedUser)
            val savedUser = userDao.findUserByLogin(login)
            requireNotNull(savedUser) { "Cached user not found ($login)" }

            val shortName = "cacheProject"
            val cachedProject = Project(
                shortName = shortName,
                fullName = null,
                ownerId = savedUser.userId!!,
                entityStatus = ES_PROJECT_ACTUAL,
                createDate = now, modifyDate = now, closeDate = null,
            )

            val cache = getTestingCache()
            verifyCacheManagerType()
            cache?.get(shortName)?.get() shouldBe null

            // create project
            projectDao.saveProject(cachedProject)

            // find in cache
            val storedProject = projectDao.findProjectByShortName(shortName)
            requireNotNull(storedProject) { "Cached project not found ($shortName)" }
            storedProject.projectId shouldNotBe null

            // verify redis cache via base class helper
            verifyKeyInRedis(shortName)

            val cachedObject = getTestingCache()?.get(shortName)?.get() as? Project
            cachedObject shouldNotBe null
            cachedObject?.shortName shouldBe shortName

            // reset cache
            projectDao.saveProject(storedProject.copy(fullName = "cacheProjectFullName"))

            val nullObject = getTestingCache()?.get(shortName)?.get() as? Project
            nullObject shouldBe null
        }
    }
}
