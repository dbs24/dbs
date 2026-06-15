package org.dbs.rest.validation

import org.dbs.rest.api.nio.DomainCommand
import org.dbs.validator.Field
import java.util.regex.Pattern
import kotlin.reflect.KProperty1

interface ValidationPattern<T: DomainCommand> {

    val rules: Collection<FieldValidationRule<T>>

    val requireAllFieldsValidated: Boolean get() = true

    infix fun <T : DomainCommand> KProperty1<T, *>.matches(fld: Pair<Pattern, Field>) =
        FieldValidationRule(
            property = this,
            pattern = fld.first,
            isOptional = this.returnType.isMarkedNullable,
            field = fld.second,
            getter = { this.get(it) }
        )


}