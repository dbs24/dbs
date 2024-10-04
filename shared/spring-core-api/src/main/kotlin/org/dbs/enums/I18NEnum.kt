package org.dbs.enums

import org.dbs.consts.DefaultMessageValue

enum class I18NEnum(
    val defaultMsgValue: DefaultMessageValue,
) {
    /*
    * -------------------------------RULES--------------------------------------
    * 1. Start from the default message:
    *    1.1 EXIST_*NAME OF FIELD* - if common field already exists
    *    1.2 EXIST_*SHORT NAME OF ENTITY*_*NAME OF FIELD* - if this field in come entity already exists
    *    1.2 FLD_UNKNOWN_*SHORT NAME OF ENTITY*_*NAME OF FIELD* - if FIELD in some entity is not known
    *    1.3 FLD_UNKNOWN_*NAME OF FIElD* - if common FIELD is not known
    *    1.4 ENT_UNKNOWN_*NAME OF ENTITY* - if this ENTITY is not known
    *    1.5 ST_UNKNOWN_*NAME OF ENTITY* - if this STATUS is not known
    *    1.6 ST_CLOSED(EXPIRED)_*NAME OF ENTITY* - if this STAT is closed, expired and etc.
    *    1.7 ST_ILLEGAL_*NAME OF ENTITY* - if this STATUS is illegal for this entity
    * */



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // COMMON
    FLD_INVALID_USER_PASSWORD("Invalid user password"),
    INVALID_LOGIN_OR_PASSWORD("Invalid login or password"),
    ENTITY_NOT_FOUND_BY_CODE("Entity not found by code (%s)"),
    ENTITY_CODE_EXISTS("Entity code (%s) already exists"),
    NOT_FOUND_ENTITY("Entity isn`t found"),
    INVALID_IP_ADDRESS("IP address is invalid or empty ({0})"),
    VENDOR_MUST_OWN_ITEMS("Vendor must own all of the input order items"),
    CUSTOMER_NOT_FOUND("Customer not found, schoolUserId"),
    BOTH_PASSWORDS_ARE_EQUAL("Both passwords are equal"),
    BOTH_PASSWORDS_ARE_NOT_EQUAL("Both passwords are not equal"),
    NEW_PASSWORD_IS_NOT_ALLOWED_PREVIOUSLY_USED("New password is not allowed (previously used)"),
    CANNOT_RESET_PASSWORD("Cannot reset own password for"),
    CANNOT_ASSIGN_PASSWORD("Cannot assign new password because current password is not expired"),
    CANNOT_ACCESS_TO_USER_ACCOUNTS_V1("Cannot access to user accounts V1"),
    CANNOT_ACCESS_TO_THIS_VENDOR("Cannot access to this vendor"),
    PASSWORD_IS_NOT_SPECIFIED_FOR_MANAGER("Password is not specified for manager"),
    OR_USER_NOT_OWNS_THIS_ACCOUNT("Or user not owns this account"),
    USER_DOES_NOT_OWN_ACCOUNT("User does not own this account"),
    ACTUAL_STATUS("Actual"),
    EXPECTED_STATUS("Expected"),
    CLOSED_STATUS("Status of item is closed"),
    INVALID_STATUS("Status of item is invalid"),
    MANDATORY_PARAM_PATTERN_NOT_FOUND_OR_EMPTY("Mandatory param not found or empty:"),
    EXIST_USER_LOGIN("USER LOGIN IS EXISTS"),
    EXIST_USER_EMAIL("EXIST USER EMAIL ({0})"),
    FLD_UNKNOWN_USER_LOGIN("UNKNOWN USER LOGIN ({0})"),
    // AUTH
    FLD_INVALID_JWT("Invalid or inactive user jwt ({0})"),
    UNKNOWN_JWT("Unknown user jwt ({0})"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // SERVICE GROUP
    FLD_SERVICE_GROUP_CODE_EXISTS("Service group code already exists"),
    FLD_SERVICE_GROUP_NAME_EXISTS("Service group name already exists"),
    FLD_SERVICE_GROUP_CODE_NOT_EXISTS("Service group code not exists"),
    ST_SERVICE_GROUP_HAS_ATTACHED_VENDORS("Can't update serviceGroup status as it has attached vendors"),
    MSG_SERVICE_GROUP_MANAGER_FORBIDDEN("The manager doesn't have the necessary privilegies, and service group"),

    // VENDOR INVITE
    FLD_VENDOR_INVITE_CODE_NOT_EXISTS("Vendor invite code not exists"),
    FLD_INVALID_VENDOR_INVITE_STATUS("Vendor invite status is invalid"),
    FLD_INVALID_VENDOR_INVITE_ACTIVATION_TIME("Vendor invite activation time is invalid"),

    // region JWT
    FLD_INVALID_MANAGER_LOGIN_IN_JWT_CLAIMS("Invalid manager login in jwt claims"),
    FLD_INVALID_VENDOR_LOGIN_IN_JWT_CLAIMS("Invalid vendor login in jwt claims"),
    FLD_INVALID_USER_LOGIN_IN_JWT_CLAIMS("Invalid user login in jwt claims"),
    FLD_INVALID_USER_ID_IN_JWT_CLAIMS("Invalid user id in jwt claims"),
    FLD_INVALID_FIN_MANAGER_LOGIN_IN_JWT_CLAIMS("Invalid fin manager login in jwt claims"),
    JWT_IS_MISSING("jwt is missing"),
    JWT_CLAIM_IS_MISSING("Claim [%s] is missing in jwt token (%s)"),
    INCORRECT_JWT("Incorrect jwt"),
    JWT_WAS_EXPIRED("jwt was expired"),
    // endregion

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    FLD_ILLEGAL_ORDER_ITEM_STATUS("Illegal order item status for order item code "),
    FLD_ORDER_ITEM_WAS_EXPIRED("order item was expired, can't create dispute "),
    FLD_APPLICATION_DISPUTE_ALREADY_EXISTS(
        "You can`t create a new application with this order item, " +
                "because the same application dispute already exists"
    ),
    FLD_APPLICATION_DISPUTE_WAS_CLOSED("Application dispute with code was closed"),

    ///////////////////////////////////////////////////////LMS//////////////////////////////////////////////////////////
    FLD_INVALID_QUIZ_ID("Invalid quiz id"),

    ///////////////////////////////////////////////////////QUIZ/////////////////////////////////////////////////////////
    FLD_INVALID_QUIZ_REQUEST_CODE("Invalid quiz request code"),
    FLD_INVALID_QUIZ_RESPONSE("Invalid quiz response"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // COMMON
    FLD_INVALID_FIELD_LENGTH("invalid field length"),
    FLD_INVALID_FIELD_MEASURE_4_VALUE("Invalid field value for measure"),
    FLD_INVALID_FIELD_VALUE_4_PATTERN("Invalid field value for pattern"),
    FLD_INVALID_FIELD_VALUE_MEASURE("Invalid value measure"),
    FLD_INVALID_BIG_DECIMAL_DIGITS("Invalid bigDecimal value, digits after comma is greater than"),
    FLD_INVALID_BIG_DECIMAL_VALUE("Invalid bigDecimal value"),
    FLD_INVALID_OLD_PASSWORD_PROVIDED("Invalid old password provided"),

    // SHIPMENT
    FLD_INVALID_SHIPMENT_DELIVERY_DATE("Invalid shipment delivery date"),

    // PRODUCT PRICE
    FLD_INVALID_PRODUCT_PRICE_FOR_CUSTOMER(
        "Invalid product price for this customer." +
                "This customer didn`t buy this product from this this vendor"
    ),

    // REVIEW PRODUCT
    FLD_INVALID_REVIEW_PRODUCT_IMAGES("Invalid number of review product images"),
    FLD_INVALID_REVIEW_PRODUCT_CODE("Invalid review product code"),
    FLD_INVALID_REVIEW_PRODUCT_EXPIRED_TIME(
        "Invalid review product updating time for customer," +
                "only 25 hours"
    ),
    FLD_INVALID_REVIEW_PRODUCT_STATUS("Invalid review product status (%s)"),

    // WAREHOUSE
    FLD_INVALID_WAREHOUSE_NEW_CODE("Invalid warehouse new code"),
    FLD_INVALID_WAREHOUSE_OLD_CODE("Invalid warehouse old code"),
    FLD_WAREHOUSE_CODE_EXISTS("Warehouse code already exists"),
    FLD_WAREHOUSE_CODE_NOT_EXISTS("Warehouse code not exists"),

    //////////////////////////////////////////////////////BANKING///////////////////////////////////////////////////////
    // MANAGER
    FLD_INVALID_FIN_MANAGER_PASSWORD("Password should specified for new fin manager"),
    FLD_UNKNOWN_FIN_MANAGER_LOGIN("Fin manager not found, manager login"),

    // DICTIONARY
    FLD_UNKNOWN_TRANSACTION_KIND_CODE("Unknown transaction kind code"),
    FLD_UNKNOWN_TRANSACTION_STATUS_CODE("Unknown transaction status code"),
    FLD_UNKNOWN_TRANSACTION_STATUS_NAME("Unknown transaction status name"),

    // CARD TRANSACTION
    FLD_UNKNOWN_CARD_TRANSACTION_CODE("Unknown card transaction code"),

    // ACCOUNT TRANSACTION
    FLD_UNKNOWN_ACCOUNT_TRANSACTION_CODE("Unknown account transaction code"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // COMMON
    FLD_UNKNOWN_EMAIL_FOR_DISPUTE_CODE("Email is not specified for disputeCode"),

    /////////////////////////////////////////////////////CATALOG////////////////////////////////////////////////////////
    // VALUES
    FLD_UNKNOWN_VALUES_FOR_PRODUCT("Unknown values for product"),

    // ATTRIBUTE
    FLD_UNKNOWN_ATTRIBUTE_CODE("Unknown attribute code"),
    FLD_UNKNOWN_BATCH_ATTRIBUTE("Unknown batchProduct2Attribute"),
    EXIST_PRODUCT_ATTRIBUTE_CODE("Product attribute code exists, code"),
    ATTRIBUTE_NAME_EXISTS("Attribute name (%s) exists"),
    ATTRIBUTE_LIST_IS_EMPTY("Attribute list is empty"),

    ///////////////////////////////////////////////////////LMS//////////////////////////////////////////////////////////
    FLD_UNKNOWN_USER_ID_QUIZ_ID("Unknown user and quiz ids"),
    FLD_UNKNOWN_STORE_USER_ID("Unknown store user id"),
    FLD_UNKNOWN_MOODLE_REQUEST_CODE("Unknown moodle request code"),

    // COMMON
    TEST_IS_CORRECT("Test is correct"),

    ///////////////////////////////////////////////////////STORE////////////////////////////////////////////////////////
    // TOPIC
    FLD_UNKNOWN_TOPIC_CODE("Unknown topic code"),

    // ORDER
    FLD_UNKNOWN_ORDER_CODE("unknown order code"),
    FLD_ORDER_ITEMS_NOT_FOUND_OR_ACCESS_DENIES("order items not found or access denied"),

    // ORDER ITEM
    FLD_UNKNOWN_ORDER_ITEM_CODE("unknown order item code"),

    // CURRENCY
    FLD_UNKNOWN_CURRENCY_ISO("Unknown currency iso"),
    FLD_UNKNOWN_CURRENCY("Unknown currency"),

    // COUNTRY
    FLD_UNKNOWN_COUNTRY("Unknown country"),

    // REGION
    FLD_UNKNOWN_REGION("Unknown region"),

    // VENDOR
    FLD_UNKNOWN_VENDOR_LOGIN("Vendor doesn`t exist, vendor login"),
    FLD_INVALID_VENDOR_LOGIN("Vendor login is invalid, vendor login"),
    FLD_INVALID_VENDOR_PUBLIC_NAME("Vendor public name is invalid, vendor public name"),
    EXIST_VENDOR_LOGIN("Vendor login [%s] already exists"),
    EXIST_VENDOR_EMAIL("Vendor email already exists"),
    EXIST_VENDOR_PUBLIC_NAME("Vendor public name already exists"),
    INVALID_REFERRAL_PROGRAM_CODE("Invalid vendor referral program code"),

    // OFFICE
    FLD_UNKNOWN_OFFICE_CODE("Unknown office code"),

    // STORE ACCOUNT
    FLD_UNKNOWN_STORE_ACCOUNT_CODE("Unknown store account code"),

    // SHIPMENT
    FLD_UNKNOWN_SHIPMENT_CODE("Unknown shipment code"),

    // PAYMENT
    FLD_UNKNOWN_PAYMENT_NUM("Unknown payment num"),

    // STRIPE PAYMENT
    FLD_PAYMENT_ALREADY_EXISTS("Payment already exists"),

    // DELIVERY POINT
    FLD_UNKNOWN_DELIVERY_POINT_CODE("Unknown delivery point code"),

    // MANAGER
    FLD_UNKNOWN_MANAGER_LOGIN("Manager not found, manager login"),
    FLD_INVALID_MANAGER_PASSWORD("Password should specified for new manager"),

    // PLAYER
    FLD_UNKNOWN_PLAYER_LOGIN("Player not found, player login ({0})"),
    FLD_INVALID_PLAYER_PASSWORD("Player should specified for new player"),
    FLD_UNKNOWN_PLAYER_LOGIN_OR_PASSWORD("Invalid player login or password provided"),
    FLD_INVALID_PLAYER_STATUS("Invalid player status: ({0})"),
    EXIST_PLAYER_LOGIN("Player login already exists"),
    EXIST_PLAYER_EMAIL("Player email already exists"),
    // INVITE
    FLD_UNKNOWN_INVITE_CODE("Invite not found ({0})"),

    // INVOICE
    FLD_UNKNOWN_INVOICE_CODE("Unknown invoice num"),

    // CUSTOMER
    CUSTOMER_UNKNOWN_LOGIN("unknown customer login"),
    CUSTOMER_PRIVILEGES_IS_EMPTY("customer privileges list is empty"),
    FLD_UNKNOWN_CUSTOMER_ACCOUNT_CODE("Unknown customer code"),
    FLD_CUSTOMER_MISSING_PASSWORD("School customer passwords is missing"),

    // APPLICATION DISPUTE
    FLD_UNKNOWN_APPLICATION_DISPUTE_CODE("Unknown application dispute code"),

    // ABSTRACT PRODUCT CATEGORY
    FLD_UNKNOWN_ABSTRACT_PRODUCT_CATEGORY("Unknown abstract product category"),

    // ABSTRACT PRODUCT
    FLD_UNKNOWN_ABSTRACT_PRODUCT_CODE("Unknown abstract product code"),

    // CART
    FLD_UNKNOWN_CART("Unknown cart"),
    CART_IS_EMPTY("Cart is empty"),

    // ISSUE POINT
    FLD_UNKNOWN_ISSUE_POINT_CODE("Unknown issuePoint code"),

    // DEPARTMENT
    FLD_UNKNOWN_DEPARTMENT_CODE("Unknown department code"),

    // POST
    FLD_UNKNOWN_POST_CODE("Unknown post code"),

    // SERVICE GROUP CODE
    FLD_UNKNOWN_SERVICE_GROUP_CODE("Unknown service group code"),
    FLD_INVALID_SERVICE_GROUP_CODE_4_DELETE("Cannot delete default service group, code"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // COMMON
    // ...
    ///////////////////////////////////////////////////////STORE////////////////////////////////////////////////////////
    // STORE ACCOUNT
    EXIST_STORE_ACCOUNT_CODE("Store account exists, code "),

    // MANAGER
    EXIST_MANAGER_LOGIN("Manager login already exists"),
    EXIST_MANAGER_EMAIL("Manager email already exists"),

    //////////////////////////////////////////////////PRODUCT CATALOG///////////////////////////////////////////////////
    // PARAM GROUP
    EXIST_PARAM_GROUP_CODE("Param group code exists, code"),
    PARAM_GROUP_NAME_EXISTS("Param group name (%s) already exists"),

    // ABSTRACT PRODUCT
    EXIST_ABSTRACT_PRODUCT_CODE("Abstract product code exists, code"),
    EXIST_ABSTRACT_PRODUCT_SHORT_NAME("Abstract product short name exists, short name"),
    UNKNOWN_ABSTRACT_PRODUCT_CODE("Unknown abstract product code "),

    // PRODUCT
    EXIST_PRODUCT_SKU("Product sku exists, sku"),
    FLD_UNKNOWN_PRODUCT_SKU("Unknown product sku"),
    ILLEGAL_PRODUCT_SKU_4_VENDOR("product sku {0} is not allowed for vendor ({1})"),

    // Manufacture
    FLD_UNKNOWN_MANUFACTURER_CODE("Manufacturer doesn`t exist, manufacturer code"),
    EXIST_MANUFACTURE_CODE("Manufacture code exists, code"),
    MANUFACTURE_NAME_EXISTS("Manufacture name (%s) already exists"),

    //////////////////////////////////////////////////////BANKING///////////////////////////////////////////////////////
    EXIST_FIN_MANAGER_LOGIN("Fin manager login already exists"),
    EXIT_FIN_MANAGER_EMAIL("Fin manager email already registered"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ST_ILLEGAL_MANAGER("Illegal manager status"),
    ST_ILLEGAL_INVOICE("Illegal invoice status"),

    // OrderItem
    ST_ILLEGAL_ORDER_ITEM("Illegal order item status"),

    // Order
    ST_ILLEGAL_ORDER_STATUS_FOR_PAY("Illegal order status for pay"),

    // Order
    ST_UNKNOWN_ORDER("Unknown order"),
    ST_UNKNOWN_ORDER_PAYMENT_SYSTEM_CODE("Unknown payment order system code"),
    ST_INVALID_PAYMENT_SUM("Invalid payment sum"),

    // MANAGER
    MANAGER_UNKNOWN_MANAGER_LOGIN("unknown manager login"),
    MANAGER_PASSWORD_NOT_SPECIFIED("password was not specified"),
    MANAGER_PASSWORD_WAS_EXPIRED("password was expired"),
    MANAGER_PRIVILEGES_IS_EMPTY("manager privileges list is empty"),

    // Privileges
    PV_UNKNOWN_PRIVILEGE_CODE("unknown privilegeCode"),

    // FIN_MANAGER
    FIN_CUSTOMER_ACCOUNT_EXISTS("customer account already exists"),
    FIN_CUSTOMER_ACCOUNT_NOT_EXISTS("customer account not exists"),
    FIN_CUSTOMER_ACCOUNT_NAME_ALREADY_EXISTS("customer account name already exists"),
    FIN_CUSTOMER_ACCOUNT_NAME_NOT_EXISTS("customer account name not exists"),
    FIN_INVALID_CUSTOMER_ACCOUNT_DETAILS(
        "invalid customer account details: " +
                "s3customerId or oldAccount should by specified"
    ),
    FIN_INVALID_CUSTOMER_ACCOUNT_DETAILS2(
        "invalid customer account details: " +
                "s3customerId and oldAccount cannot be provided"
    ),
    // GRPC
    GRPC_PROCEDURE_NAME_IS_NOT_ASSIGNED("procedure name is not assigned"),
    GRPC_REMOTE_ADDRESS_IS_NOT_ASSIGNED("remote address is not assigned {0}"),
    GRPC_USER_AGENT_IS_NOT_ASSIGNED("user agent is not assigned"),

    // QUIZ
    EXISTS_TEMPLATE_CODE("Template code already exists"),
    UNKNOWN_TEMPLATE_CODE("Unknown template code"),
    UNKNOWN_GROUP_CODE("Unknown template group code"),
    QUIZ_TC_KEYWORD_NOT_FOUND("Keyword missing: "),
    QUIZ_TC_ILLEGAL_QUESTION_FORMAT("Illegal question format: "),
    QUIZ_TC_ILLEGAL_QUESTION_DUPLICATE("Illegal question duplicate: "),
    QUIZ_TC_ILLEGAL_ANSWERS_FORMAT("Illegal answers format: "),
    QUIZ_TC_ILLEGAL_ANSWERS_COUNT("question contains only one answer: "),
    QUIZ_TC_ILLEGAL_FIRST_SYMBOL("answer contains illegal symbol: "),
    QUIZ_TC_ILLEGAL_TEMPLATE_NAME("Illegal template name: "),

    // SCHOOL
    UNKNOWN_BUILDING_CODE("Unknown building code"),
    UNKNOWN_BUILDING_SCHEME_CODE("Unknown building scheme code"),
    UNKNOWN_BUILDING_SCHEME_REGION_CODE("Unknown building scheme region code"),

    // Referral program
    UNKNOWN_REFERRAL_LINK("Unknown referral link"),
    UNKNOWN_REFERRER_CODE("Unknown referrer code"),
    UNKNOWN_REFERRAL_CODE("Unknown referral code"),
    INVALID_REFERRAL_CODE("Invalid referral code"),
    INVALID_BONUS_SUM("Invalid bonus sum"),
    INVALID_BONUS_DATE("Invalid bonus date"),
    REFERRER_IS_NOT_ALLOWED("Referrer is not allowed"),
    REFERRAL_IS_ALREADY_LINKED("Referral is already linked"),

    // region Workshop
    WORKSHOP_ALREADY_COMPLETED("Workshop has already been held"),
    // endregion

    // Invite
    UNKNOWN_INVITE_CODE("Unknown invite code"),
}
