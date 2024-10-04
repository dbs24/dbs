/*
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.dbs.security.jwt

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*
import java.util.function.Predicate

/**
 * Decides when a JWT string is valid.
 * First  try to parse it, then check that
 * the signature is correct.
 * If something fails an empty Mono is returning
 * meaning that is not valid.
 * Verify that expiration date is valid
 */
class JWTCustomVerifier {
    private val jwsVerifier: JWSVerifier = buildJWSVerifier() as JWSVerifier
    fun check(token: String): Mono<SignedJWT> = Mono.justOrEmpty(createJWS(token))
        .filter(isNotExpired)
        .filter(validSignature)

    private val isNotExpired =
        Predicate { token: SignedJWT -> getExpirationDate(token).after(Date.from(Instant.now())) }
    private val validSignature = Predicate { token: SignedJWT ->

        var isValidSignature = false

        try {
            isValidSignature = token.verify(jwsVerifier)
        } catch (e: JOSEException) {
            e.printStackTrace()
        }

        isValidSignature
    }

    private fun buildJWSVerifier(): MACVerifier? = try {
        MACVerifier(JWTSecrets.DEFAULT_SECRET)
    } catch (e: JOSEException) {
        println(e)
        null
    }


    private fun createJWS(token: String): SignedJWT = SignedJWT.parse(token)


    private fun getExpirationDate(token: SignedJWT): Date = token.jwtClaimsSet.expirationTime

}
