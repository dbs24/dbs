package org.dbs.validator

import org.dbs.enums.RefEnum


enum class Error(private val value: String, private val code: Int) : RefEnum {

    GENERAL_ERROR("general.error", 1),
    MONITORING_ERROR_STARTING_APPLICATION("app.error", 2),
    ENTITY_NOT_FOUND("entity.not.found", 10),
    INVALID_ENTITY_STATUS("entity.status.invalid", 20),
    INVALID_ENTITY_PASSWORD("entity.password.invalid", 21),
    GRPC_INTERNAL_ERROR("grpc.general.error", 30),
    GRPC_SERVICE_IS_UNAVAILABLE("grpc.service.unavailable", 31),
    GRPC_JWT_IS_NOT_AUTHORIZED("grpc.service.not.authorized", 32),

    ALREADY_EXISTS("already.exists", 50),
    ILLEGAL_CALL("illegal.call", 1000),
    H1H2_ILLEGAL_CALL("h1h2.illegal.call", 1001),
    ILLEGAL_STATE("illegal.state", 1010),
    INVALID_QUERY_PARAM("invalid.query.param", 100000),
    INVALID_JWT("invalid.jwt", 100002),
    INVALID_ENTITY_ATTR("entity.attrs.invalid", 100001),
    INVALID_DTO_ATTR("entity.dto.invalid", 100003),
    INVALID_ATTR_PATTERN_MISMATCH("invalid attribute value ", 100101),
    QUERY_PARAM_NOT_FOUND("query param not exists - ", 100102),
    FIELD_NOT_FOUND("mandatory field is not defined - ", 100103),
    INVALID_FIELD_VALUE("invalid or unknown field value - ", 100201),
    MANDATORY_FIELD_IS_NULL("mandatory field is null - ", 100301),
    UNKNOWN_LOGIN_OR_PASSWORD("unknown login or password ", 100501),
    USER_LOGIN_ALREADY_EXISTS("user login already exist ", 200101),
    USER_DOES_NOT_EXISTS("unknown used ", 200105),
    USER_LOGINS_MISMATCH("user logins mismatch ", 200110),
    USER_PASSWORDS_MISMATCH("user passwords mismatch ", 200120),
    USER_ACCESS_DENIED("user access denied ", 200130),
    USER_INVALID_ACCESSTOKEN("invalid access token", 200140),
    USER_HAS_NO_ROLE("user has no role", 200150),

    //==========================================================================
    VENDOR_INVALID_STATUS("vendor status invalid", 300100),
    VENDOR_LOGIN_ALREADY_EXISTS("vendor login already exist", 300101),
    VENDOR_ACCESS_TOKEN_NOT_FOUND("vendor access token not exist", 300102),
    VENDOR_PASSWORDS_MISMATCH("vendor passwords mismatch ", 300103),
    VENDOR_UNKNOWN_LOGIN("unknown vendor login", 300104),
    MANAGER_UNKNOWN_LOGIN("unknown manager login", 300200),
    MANAGER_PASSWORDS_MISMATCH("manager passwords mismatch ", 300201),
    MANAGER_UNKNOWN_LAST_NAME("unknown manager last name", 300202),
    MANAGER_PRIVILEGES_IS_EMPTY("manager privileges is empty", 300203),
    MANAGER_PASSWORD_WAS_EXPIRED("manager password was expired", 300204),
    MANAGER_INVALID_STATUS("manager status invalid", 400100),
    WAREHOUSE_INVALID_STATUS("warehouse status invalid", 500100),
    QP_INVALID_QUERY_PARAM("invalid query param", 900100),


    //=============================================================================
    CUSTOMER_ACCOUNT_ALREADY_EXISTS("customer account already exists", 1000100),
    CUSTOMER_INVALID_STATUS("customer status is invalid", 1000101),
    CUSTOMER_PASSWORDS_MISMATCH("customer passwords mismatch", 1000102),
    CUSTOMER_ACCOUNT_NOT_EXISTS("customer account not exists", 1000103),
    CUSTOMER_ACCOUNT_NAME_ALREADY_EXISTS("customer account name already exists", 1000104),
    CUSTOMER_ACCOUNT_NAME_NOT_EXISTS("customer account name not exists", 1000105),
    INVALID_CUSTOMER_ACCOUNT_DETAILS("invalid customer account details", 1000106),
    //==========================================================================
    // Order
    ORDER_PAYMENT_SYSTEM_CODE_IS_UNKNOWN("unknown payment order system code", 900200),
    //==========================================================================
    // Payment
    STRIPE_PAYMENT_ERROR("generic payment error", 1000201),
    STRIPE_CHECKOUT_ERROR("stripe checkout error", 1000202),
    //==========================================================================
    // Player
    PLAYER_PASSWORDS_MISMATCH("player password mismatch", 1200100),
    PLAYER_INVALID_STATUS("invalid player status", 1200110),
    FEN_INVALID_DEPTH_VALUE("invalid depth value", 1200111),
    FEN_INVALID_TIMEOUT_VALUE("invalid timeout value", 1200112),
    ;

    override fun getValue() = this.value
    override fun getCode() = this.code
}
