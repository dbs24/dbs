package org.dbs.test

import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties =
    ["config.security.profile.webfilter.chain=development", "config.wa.contract.deprecated.update=2147483648"]
)
abstract class AbstractAuthTest /*: AbstractWebTest()*/
