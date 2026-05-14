package org.dbs.service.repo

import org.dbs.entity.core.Incident
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface IncidentRepo : CoroutineCrudRepository<Incident, String>