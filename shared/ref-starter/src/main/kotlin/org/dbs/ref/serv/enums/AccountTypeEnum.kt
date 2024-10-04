package org.dbs.ref.serv.enums

import java.util.stream.Collectors

enum class AccountTypeEnum(
    val accountTypeId: Int,
    val typeName: String,
    val description: String
) {
    CORRESPONDENT(10, "Correspondent", "Correspondent bank account"), // корреспондентский
    CURRENT(20, "Current", "Current bank account"), // текущий банковский аккаунт
    TEMPORARY(30, "Temporary", "Temporary bank account"), // временный
    CHARITABLE(40, "Charitable", "Charitable bank account"), // благотворительный
    DEPOSIT(50, "Deposit", "Deposit bank account"), // депозитный
    BANK_OPERATIONS(
        60,
        "Bank operations",
        "Account for accounting for funds in settlements with banks"
    ), // для операций с банками
    CUSTOMER_OPERATIONS(
        70,
        "Customer operations",
        "Account for accounting for funds in settlements with customers"
    ), // для операций с клиентами
    OTHER(100, "Other", "Other bank account"), // иной
    ;

    companion object {
        fun getEnum(code: Int): AccountTypeEnum =
            entries.stream().filter { it.accountTypeId == code }.findFirst().orElseThrow()

        fun getEnumNullable(code: Int): AccountTypeEnum? =
            entries.asSequence().filter { it.accountTypeId == code }.firstOrNull()

        fun isExistEnum(id: Int): Boolean = AccountTypeEnum.entries.filter { it.accountTypeId == id }.any()

        val accountTypeIds: Collection<Int>
            get() = entries.stream()
                .map(AccountTypeEnum::accountTypeId)
                .collect(Collectors.toList())
    }
}
