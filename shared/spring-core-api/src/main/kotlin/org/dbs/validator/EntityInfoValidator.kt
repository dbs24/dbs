package org.dbs.validator

import org.dbs.spring.core.api.EntityInfo


interface EntityInfoValidator<T : EntityInfo> : Validator<T>
