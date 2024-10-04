package org.dbs.consts

import org.springframework.web.reactive.function.client.ClientResponse

typealias WebClientOnStatusProcessor = (ClientResponse) -> Unit
