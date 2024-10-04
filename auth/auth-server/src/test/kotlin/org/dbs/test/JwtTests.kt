package org.dbs.test

import org.dbs.auth.server.AuthServer
import org.dbs.auth.server.config.AuthorizationServerConfig
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@AutoConfigureWebTestClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [AuthServer::class])
@Import(AuthorizationServerConfig::class)
@TestInstance(PER_CLASS)
@TestMethodOrder(
    MethodOrderer.OrderAnnotation::class
)
class JwtTests : AbstractAuthTest() {
//    @Order(100)
//    @Test //@RepeatedTest(100)
//    fun createJwt() {
//        val monoUserSessionInfo = StmtProcessor.create(
//            WaUserSessionInfo::class.java
//        ) { usi ->
//            //user.set
//            usi.setDeviceId(generateTestInteger(1, 1000000))
//            usi.setAppName(generateTestString15())
//            usi.setAppVersion(generateTestString15())
//            usi.setFcmToken(generateTestString15())
//        })
//        runTest {
//            //logger.info("testing {}", URI_LOGIN);
//            val token: String =
//                webTestClient //                .mutate().filter(basicAuthentication(this.uid, this.pwd)).build()
//                    .post()
//                    .uri { uriBuilder ->
//                        uriBuilder
//                            .path(URI_LOGIN)
//                            .build()
//                    }
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_JSON)
//                    .body(monoUserSessionInfo, WaUserSessionInfo::class.java)
//                    .exchange()
//                    .expectStatus()
//                    .isOk()
//                    .expectBody(LoginPaymentResult::class.java)
//                    .returnResult()
//                    .getResponseBody()
//                    .getAccessToken()
//        }
//    }
}
