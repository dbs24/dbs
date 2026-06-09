package org.dbs.tree

import com.google.protobuf.MessageLite
import io.grpc.ManagedChannel
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.test.ko.BaseGrpcSpec
import org.dbs.tree.client.ProjectServiceGrpcKt
import org.dbs.tree.client.UserServiceGrpcKt
import org.dbs.tree.repo.ProjectRepo
import org.dbs.tree.repo.UserRepo
import org.dbs.utils.lateInitProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

typealias UserStub = UserServiceGrpcKt.UserServiceCoroutineStub
typealias ProjectStub = ProjectServiceGrpcKt.ProjectServiceCoroutineStub

abstract class BaseTreeGrpcTest : BaseGrpcSpec() {

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var projectRepo: ProjectRepo

    var userStub: UserStub by lateInitProperty()
    var projectStub: ProjectStub by lateInitProperty()

    override fun initStubs(channel: ManagedChannel) {
        userStub = UserStub(channel)
        projectStub = ProjectStub(channel)
    }

    @Suppress(UNCHECKED_CAST)
    inline fun <B : MessageLite.Builder, M : MessageLite> B.build(block: B.() -> Unit): M =
        apply(block).build() as M

}
