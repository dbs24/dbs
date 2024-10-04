package org.dbs.consts

import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.DEFAULT_PAGE_NUM
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.DEFAULT_PAGE_SIZE
import org.dbs.consts.SysConst.STRING_ONE
import org.dbs.consts.SysConst.STRING_TEN
import org.dbs.rest.api.ShutdownRequest
import java.util.function.Predicate

object RestHttpConsts {
    const val BEARER = "Bearer "
    const val APP_JSON = "application/json;"
    const val APP_XML = "application/xml"
    const val CONTENT_TYPE = "application/json; charset=utf-8;"
    const val SRV_READ_TIMEOUT = "30000"
    const val SRV_CONNECT_TIMEOUT = "10000"
    const val GZIP_ACCEPT_ENCODING = "gzip"
    const val IDENTITY_CONTENT_ENCODING = "identity"
    const val HTTP_200_STRING = "200"
    const val USE_GZIP_ENCODING = true
    const val NO_GZIP_ENCODING = false
    const val HTTP_200_OK_STRING = "OK"
    const val COMMON_SWAGGER_TAG = "Common routes"
    const val URI_HTTP = "h" + "t" + "t" + "p" + "://"
    const val URI_HTTPS = "https://"
    const val URI_LOCALHOST = "127.0.0.1"
    const val URI_LOCALHOST_DOMAIN = "localhost"
    const val URI_LOCALHOST_4_TEST = "localhost/test"
    const val URI_LOCALHOST_NONE = "0"
    const val GOOGLE_COM = "google.com"
    const val URI_GOOGLE = "$URI_HTTPS$GOOGLE_COM"
    const val URI_LOCAL_COMMON_ADDR_MASK = "192.168."
    const val URI_IP = "ip"
    const val URI_MAC = "mac"
    const val URI_HTTP_LOCALHOST = URI_HTTP + URI_LOCALHOST
    const val URI_HTTPS_LOCALHOST = URI_HTTPS + URI_LOCALHOST
    val LOCAL_ADDRESSES = arrayOf(URI_LOCALHOST, URI_HTTP_LOCALHOST, URI_HTTPS_LOCALHOST)
    const val URI_API = "/api"

    object RouteVersion {
        const val URI_V1 = "/v1"
        const val URI_V2 = "/v2"
    }

    object RouteAction {
        const val URI_CREATE = "/create"
        const val URI_UPDATE = "/update"
        const val URI_RESET = "/reset"
        const val URI_CREATE_OR_UPDATE = "/createOrUpdate"
        const val URI_GET = "/get"
        const val URI_LIST= "/list"
        const val URI_UPLOAD = "/upload"
        const val URI_DOWNLOAD = "/download"
        const val URI_REMOVE = "/remove"
        const val URI_ATTACH = "/attach"
        const val URI_DETACH = "/detach"
    }

    object RouteConsts {
        const val URI_IMAGE = "/image"
        const val URI_STATUS = "/status"
        const val URI_FOR = "/for"
    }

    const val URI_STARTED = URI_API + "/started"
    const val URI_LIVENESS_API = "/liveness"
    const val ROUTE_URI_LIVENESS = URI_API + URI_LIVENESS_API
    const val URI_READINESS_API = "/readiness"
    const val ROUTE_URI_READINESS = URI_API + URI_READINESS_API
    const val URI_CAN_SHUTDOWN = URI_API + "/canShutdown"
    const val URI_LOGIN = "/login"
    const val URI_IDS = "/ids"
    const val URI_ANON = "/anon"
    const val URI_LOGIN_API = URI_API + URI_LOGIN
    const val URI_REGISTRY = "/registry"
    const val URI_REFRESH = "/refresh"
    const val URI_VERIFY = "/verify"

    const val URI_REGISTRY_API = URI_API + URI_REGISTRY
    const val URI_JWT = "/jwt"
    const val URI_REFRESH_JWT = URI_JWT + URI_REFRESH
    const val URI_LOGIN_EXISTS = URI_LOGIN + "/exists"
    const val URI_SWAGGER_MAIN = "/swagger-ui.html"
    const val URI_SWAGGER_LINKS = "/webjars/swagger-ui/**"
    const val URI_SWAGGER_WEBJARS_LINKS = "/swagger-ui/**"
    const val URI_SWAGGER_API_DOCS = "/v3/api-docs/**"
    const val URI_SPRING_BOOT_ACTUATOR = "/actuator/**"
    const val URI_EUREKA = "/eureka/**"
    const val URI_EUREKA_INFO = "/info"
    const val URI_EUREKA_HEALTH = "/health"
    const val DEFAULT_ACCESS_ROLE = "DEFAULT_ROLE"
    val SHUTDOWN_REQUEST_CLASS = ShutdownRequest::class.java

    const val ONE_ATTEMPT = 1L

    object Headers {
        const val MANAGER_LOGIN = "managerLogin"
    }

    object RestQueryParams {
        const val QP_SORT_FIELD = "sortField"
        const val QP_SORT_ORDER = "sortOrder"
        const val QP_DATE_FROM = "dateFrom"
        const val QP_DATE_TO = "dateTo"

        const val QP_FILE_TYPE = "file"
        const val QP_STRING_TYPE = "string"
        const val QP_BOOLEAN_TYPE = "boolean"
        const val QP_DECIMAL_TYPE = "decimal"
        const val QP_LIST_INT_TYPE = "list int"
        const val QP_ARRAY_TYPE = "array"
        const val QP_INT_TYPE = "integer"
        const val QP_INT64_FORMAT = "int64"
        const val QP_ACCESS_TOKEN = "accessToken"
        const val QP_LOGIN = "login"
        const val QP_EMAIL = "email"
        const val QP_PASSWORD = "password"
        const val QP_ENTITY_CODE = "code"
        const val QP_ENTITY_IDS = "ids"
        const val QP_USER_LOGIN = "userLogin"
        const val QP_PRODUCT2ATTRIBUTE_KEYWORD = "product2AttributeKeyword"
        const val QP_ABSTRACT_PRODUCT_KEYWORD = "abstractProductKeyword"
        const val QP_ABSTRACT_PRODUCT_CODE_KEYWORD = "abstractProductCodeKeyword"
        const val QP_ROLE_CODE = "roleCode"
        const val QP_ACTION_CODE = "actionCode"
        const val QP_ENTITY_TYPE = "entityType"
        const val QP_ENTITY_STATUS = "entityStatus"
        const val QP_ENTITY_STATUSES = "entityStatuses"
        const val QP_TOKEN = "token"
        const val QP_SEARCH_MASK = "searchMask"

        //==============================
        const val QP_OFFICE_CODE = "officeCode"
        const val QP_POST_CODE = "postCode"
        const val QP_COUNTRY_ISO = "countryIso"
        const val QP_REGION_CODE = "regionCode"

        const val QP_SECRET = "secret"
        const val QP_TOKEN_RESPONSE = "response"

        object FilterParams {
            const val QP_NAME_MASK = "nameMask"
            const val QP_FROM_AGE = "fromAge"
            const val QP_TO_AGE = "toAge"
        }

        object ApplicationDisputeParams {
            const val QP_APPLICATION_DISPUTE_STATUS = "applicationDisputeStatus"
            const val QP_APPLICATION_DISPUTE_CODE_SEARCH_MASK = "applicationDisputeCodeSearchMask"
            const val QP_APPLICATION_DISPUTE_CODE = "applicationDisputeCode"
        }

        object ProductCategoryParams {
            const val QP_PRODUCT_CATEGORY_CODE_KEYWORD = "productCategoryCodeKeyword"
        }

        object ProductAttrsParams {
            const val QP_PRODUCT_ATTRIBUTE_CODE = "productAttributeCode"
            const val QP_PRODUCT_ATTRIBUTE_STATUS = "productAttributeStatus"
            const val QP_PRODUCT_ATTRIBUTE_KEYWORD = "productAttributeKeyword"
            const val QP_PRODUCT_ATTRIBUTE_CODE_KEYWORD = "productAttributeCodeKeyword"
            const val QP_PRODUCT_ATTRIBUTE_NAME_SEARCH_MASK = "productAttributeNameSearchMask"
            const val QP_PRODUCT_ATTRIBUTE_CODE_SEARCH_MASK = "productAttributeCodeSearchMask"
            const val QP_PRODUCT_ATTRIBUTE_UNIT_SEARCH_MASK = "productAttributeUnitSearchMask"
            const val QP_PRODUCT_ATTRIBUTE_GROUP_NAME_SEARCH_MASK = "productAttributeGroupNameSearchMask"
            const val QP_PRODUCT_ATTRIBUTE_PRIORITY = "productAttributePriority"
            const val QP_PRODUCT_ATTRIBUTE_VALUE_CODES = "productAttributeValueCodes"
        }

        object Manufactures {
            const val QP_MANUFACTURE_NAME_SEARCH_MASK = "manufactureNameSearchMask"
            const val QP_MANUFACTURE_CODE_SEARCH_MASK = "manufactureCodeSearchMask"
            const val QP_MANUFACTURE_CODES = "manufactureCodes"
            const val QP_MANUFACTURE_KEYWORD = "manufactureKeyword"
            const val QP_MANUFACTURE_CODE_KEYWORD = "manufactureCodeKeyword"
            const val QP_MANUFACTURE_CODE = "manufactureCode"
            const val QP_MANUFACTURE_STATUS = "manufactureStatus"
            const val QP_CATEGORY_CODE = "categoryCode"
            const val QP_LIMIT = "limit"
        }

        object ParamGroupsParams {
            const val QP_PARAM_GROUP_NAME_SEARCH_MASK = "paramGroupNameSearchMask"
            const val QP_PARAM_GROUP_CODE_SEARCH_MASK = "paramGroupCodeSearchMask"
            const val QP_PARAM_GROUP_CODE = "paramGroupCode"
            const val QP_PARAM_GROUP_PRIORITY_SEARCH_MASK = "paramGroupPrioritySearchMask"
            const val QP_PARAM_GROUP_STATUS = "paramGroupStatus"
        }

        object Products {
            const val QP_LIST_ID = "listId"
            const val QP_PRODUCT_FULL_NAME_SEARCH_MASK = "productFullNameSearchMask"
            const val QP_PRODUCT_SKU = "sku"
            const val QP_PRODUCT_SKU_SEARCH_MASK = "productSkuSearchMask"
            const val QP_PRODUCT_STATUS = "productStatus"
            const val QP_IS_NEED_COUNT_PRODUCTS = "isNeedCountProducts"
        }

        object ServiceGroupParams {
            const val QP_SERVICE_GROUP_STATUS = "serviceGroupStatus"
            const val QP_SERVICE_GROUP_CODE = "serviceGroupCode"
            const val QP_SERVICE_GROUP_CODE_MASK = "serviceGroupCodeMask"
            const val QP_SERVICE_GROUP_NAME_MASK = "serviceGroupNameMask"
        }

        object Orders {
            const val QP_ORDER_STATUS = "orderStatus"
            const val QP_ORDER_NUM = "orderNum"
            const val QP_ORDER_NUM_MASK = "orderNumMask"
        }

        object OrderItemParams {
            const val QP_ORDER_ITEM_NUM = "orderItemNum"
            const val QP_ORDER_ITEM_NUM_MASK = "orderItemNumMask"
            const val QP_ORDER_ITEM_STATUS = "orderItemStatus"
        }

        object ProductPrices {
            const val QP_PRICES_LIST_ID = "pricesListId"
            const val QP_CURRENCY = "currencyIso"
            const val QP_PRICE_SEARCH_MASK = "priceSearchMask"
            const val QP_PRODUCT_FULL_NAME_SEARCH_MASK = "productFullNameSearchMask"
            const val QP_PRODUCT_PRICE_STATUS = "productPriceStatus"
            const val QP_PRICE_FROM = "priceFrom"
            const val QP_PRICE_TO = "priceTo"
        }

        object AbstractProducts {
            const val QP_ABSTRACT_PRODUCT_KEYWORD = "abstractProductKeyword"
            const val QP_ABSTRACT_PRODUCT_CODE_KEYWORD = "abstractProductCodeKeyword"
            const val QP_ABSTRACT_PRODUCT_SHORT_NAME_SEARCH_MASK = "abstractProductShortNameSearchMask"
            const val QP_ABSTRACT_PRODUCT_CODE_SEARCH_MASK = "abstractProductCodeSearchMask"
            const val QP_ABSTRACT_PRODUCT_CATEGORY_NAME_SEARCH_MASK = "abstractProductCategoryNameSearchMask"
            const val QP_ABSTRACT_PRODUCT_STATUS = "abstractProductStatus"
        }

        object CategoryParams {
            const val QP_SUBCATEGORY_CODE = "subcategoryCode"
        }

        object DepartmentParams {
            const val QP_DEPARTMENT_CODE = "departmentCode"
            const val QP_DEPARTMENT_STATUS = "departmentStatus"
            const val QP_DEPARTMENT_CODE_SEARCH_MASK = "departmentCodeSearchMask"
            const val QP_DEPARTMENT_NAME_SEARCH_MASK = "departmentNameSearchMask"
        }

        object OfficeParams {
            const val QP_OFFICE_CODE = "officeCode"
            const val QP_OFFICE_STATUS = "officeStatus"
            const val QP_OFFICE_CODE_SEARCH_MASK = "officeCodeSearchMask"
            const val QP_OFFICE_NAME_SEARCH_MASK = "officeNameSearchMask"
        }

        object VendorInvitesParams {
            const val QP_VENDOR_INVITE_CODE = "vendorInviteCode"
            const val QP_VENDOR_INVITE_CODE_MASK = "vendorInviteCodeMask"
            const val QP_VENDOR_INVITE_EMAIL_MASK = "vendorInviteEmailMask"
            const val QP_VENDOR_INVITE_STATUS = "vendorInviteStatus"
        }

        object VendorsParams {
            const val QP_VENDOR_ID = "vendorId"
            const val QP_VENDOR_LOGIN = "vendorLogin"
            const val QP_VENDOR_LOGIN_SEARCH_MASK = "vendorLoginSearchMask"
            const val QP_VENDOR_EMAIL = "vendorEmail"
            const val QP_VENDOR_EMAIL_SEARCH_MASK = "vendorEmailSearchMask"
            const val QP_VENDOR_LAST_NAME = "vendorLastName"
            const val QP_VENDOR_LAST_NAME_SEARCH_MASK = "vendorLastNameSearchMask"
            const val QP_VENDOR_STATUS = "vendorStatus"
            const val QP_VENDOR_COUNTRY = "vendorCountry"
            const val QP_VENDOR_COUNTRY_SEARCH_MASK = "vendorCountrySearchMask"
            const val QP_VENDOR_PHONE = "vendorPhone"
            const val QP_VENDOR_PHONE_SEARCH_MASK = "vendorPhoneSearchMask"
            const val QP_VENDOR_PUBLIC_NAME_SEARCH_MASK = "vendorPublicNameSearchMask"
            const val QP_VENDOR_PUBLIC_NAME = "vendorPublicName"
        }

        object VendorAccountParams {
            const val QP_VENDOR_ACCOUNT_CODE = "vendorAccountCode"
        }

        object StoreAccountParams {
            const val QP_STORE_ACCOUNT_CODE = "storeAccountCode"
            const val QP_STORE_ACCOUNT_TYPE_MASK = "storeAccountTypeMask"
            const val QP_STORE_ACCOUNT_NAME_MASK = "storeAccountNameMask"
            const val QP_STORE_ACCOUNT_CURRENCY_ISO_MASK = "storeAccountCurrencyIsoMask"
            const val QP_STORE_ACCOUNT_STATUS = "storeAccountStatus"
        }

        object CustomerAccountParams {
            const val QP_CUSTOMER_ACCOUNT_CODE = "customerAccountCode"
            const val QP_CUSTOMER_ACCOUNT_STATUS = "customerAccountStatus"
            const val QP_CUSTOMER_ACCOUNT_LOGIN_MASK = "customerAccountLoginMask"
            const val QP_CUSTOMER_ACCOUNT_EMAIL_MASK = "customerAccountEmailMask"
            const val QP_CUSTOMER_ACCOUNT_PHONE_MASK = "customerAccountPhoneMask"
            const val QP_CUSTOMER_ACCOUNT_FIRST_NAME_MASK = "customerAccountFirstNameMask"
            const val QP_CUSTOMER_ACCOUNT_LAST_NAME_MASK = "customerAccountLastNameMask"
        }

        object ShipmentParams {
            const val QP_SHIPMENT_CODE = "shipmentCode"
        }

        object Warehouse {
            const val QP_WAREHOUSE_CODE = "warehouseCode"
            const val QP_WAREHOUSE_CODES = "warehouseCodes"
            const val QP_WAREHOUSE_CODE_SEARCH_MASK = "warehouseCodeSearchMask"
            const val QP_WAREHOUSE_NAME = "warehouseName"
            const val QP_WAREHOUSE_NAME_SEARCH_MASK = "warehouseNameSearchMask"
            const val QP_WAREHOUSE_DATE = "warehouseDate"
            const val QP_WAREHOUSE_DATE_SEARCH_MASK = "warehouseDateSearchMask"
            const val QP_WAREHOUSE_STATUS = "warehouseStatus"
            const val QP_WAREHOUSE_PHONE = "warehousePhone"
            const val QP_WAREHOUSE_PHONE_SEARCH_MASK = "warehousePhoneSearchMask"
            const val QP_WAREHOUSE_REST_TYPE = "warehouseRestType"
        }

        object CustomerParams {
            const val QP_CUSTOMER_LOGIN = "customerLogin"
            const val QP_CUSTOMER_STATUS = "customerStatus"
            const val QP_CUSTOMER_PASSWORD = "customerPassword"
        }

        object ManagersParams {
            const val QP_MANAGER_LOGIN = "managerLogin"
            const val QP_MANAGER_LOGIN_SEARCH_MASK = "managerLoginSearchMask"
            const val QP_MANAGER_FIRST_NAME = "managerFirstName"
            const val QP_MANAGER_LAST_NAME = "managerLastName"
            const val QP_MANAGER_LAST_NAME_SEARCH_MASK = "managerLastNameSearchMask"
            const val QP_MANAGER_STATUS = "managerStatus"
            const val QP_MANAGER_FIRST_NAME_SEARCH_MASK = "managerFirstNameSearchMask"
            const val QP_MANAGER_EMAIL_SEARCH_MASK = "managerEmailSearchMask"
            const val QP_MANAGER_PHONE_SEARCH_MASK = "managerPhoneSearchMask"
        }

        object RegistryApplicationParams {
            const val QP_REGISTRY_APPLICATION_CODE = "registryApplicationCode"
            const val QP_REGISTRY_APPLICATION_EMAIL_MASK = "registryApplicationEmailMask"
            const val QP_REGISTRY_APPLICATION_STATUS = "registryApplicationStatus"
        }

        object WarehouseParams {
            const val QP_WAREHOUSE_CODE = "warehouseCode"
        }

        object PostParams {
            const val QP_POST_CODE = "postCode"
            const val QP_POST_STATUS = "postStatus"
            const val QP_POST_CODE_SEARCH_MASK = "postCodeSearchMask"
            const val QP_POST_NAME_SEARCH_MASK = "postNameSearchMask"
        }

        object AccountParams {
            const val QP_ACCOUNT_CODE_MASK = "accountCodeMask"
        }

        object TransactionParams {
            const val QP_TRANSACTION_CODE = "transactionCode"
            const val QP_TRANSACTION_TYPE = "transactionType"
            const val QP_TRANSACTION_DATE_FROM = "transactionDateFrom"
            const val QP_TRANSACTION_DATE_TO = "transactionDateTo"
            const val QP_TRANSACTION_TYPE_NAME_MASK = "transactionTypeNameMask"
            const val QP_TRANSACTION_STATUS_NAME_MASK = "transactionStatusNameMask"
            const val QP_TRANSACTION_SUM = "transactionSum"
        }

        object InvoicesParams {
            const val QP_INVOICE_NUM = "invoiceNumber"
            const val QP_INVOICE_NUM_MASK = "invoiceNumberSearchMask"
            const val QP_INVOICE_STATUS = "invoiceStatus"
        }

        object ReviewProductParams {
            const val QP_REVIEW_PRODUCT_PHOTOS_FLAG = "reviewProductPhotosFlag"
            const val QP_REVIEW_PRODUCT_STATUS = "reviewProductStatus"
            const val QP_REVIEW_PRODUCT_FIRST_NAME_MASK = "reviewProductFirstNameMask"
            const val QP_REVIEW_PRODUCT_LAST_NAME_MASK = "reviewProductLastNameMask"
            const val QP_REVIEW_PRODUCT_CODE = "reviewProductCode"
        }

        object Moodle {
            const val QP_USER_ID = "userId"
            const val QP_QUIZ_ID = "quizId"
            const val QP_LMS_REQUEST_CODE = "lmsRequestCode"
            const val QP_QUIZ_NAME = "quizName"
            const val QP_LMS_FILTER_STATUS = "filterStatus"
            const val QP_LMS_FILTER_NAME = "filterName"
        }

        object WorkshopParams {
            const val QP_FOR_WITH_DISABILITIES = "forWithDisabilities"
            const val QP_CURRENT_VENDOR_WORKSHOPS = "currentVendorWorkshops"
        }

        object Pagination {
            const val DEFAULT_PAGE_SIZE = STRING_TEN
            const val DEFAULT_PAGE_NUM = STRING_ONE
            const val QP_PAGE_SIZE = "pageSize"
            const val QP_PAGE_NUM = "pageNum"

            const val PRODUCT_BY_ATTRS_DEFAULT_SORT_FIELD = "price"
            const val PRODUCT_BY_ATTRS_DEFAULT_SORT_ORDER = "ASC"

            const val PRODUCT_SIMILAR_DEFAULT_SORT_FIELD = "fullName"
            const val PRODUCT_SIMILAR_DEFAULT_SORT_ORDER = "ASC"

            const val STORE_ACCOUNTS_DEFAULT_SORT_FIELD = "createDateTime"
            const val STORE_ACCOUNTS_DEFAULT_SORT_ORDER = "DESC"

            const val ORDER_DEFAULT_SORT_FIELD = "date"
            const val ORDER_DEFAULT_SORT_ORDER = "DESC"

            const val ORDER_ITEM_DEFAULT_SORT_FIELD = "date"
            const val ORDER_ITEM_DEFAULT_SORT_ORDER = "DESC"

            const val INVOICE_DEFAULT_SORT_FIELD = "createDate"
            const val INVOICE_DEFAULT_SORT_ORDER = "DESC"

            const val PRICE_DEFAULT_SORT_FIELD = "createDate"
            const val PRICE_DEFAULT_SORT_ORDER = "DESC"

            const val REVIEW_PRODUCT_DEFAULT_SORT_FIELD = "createDate"
            const val REVIEW_PRODUCT_RATING_SORT_FIELD = "rating"
            const val REVIEW_PRODUCT_DEFAULT_SORT_ORDER = "DESC"

            const val ACC_TRANSACTION_DEFAULT_SORT_FIELD = "transactionDate"
            const val ACC_TRANSACTION_DEFAULT_SORT_ORDER = "DESC"

            const val CARD_TRANSACTION_DEFAULT_SORT_FIELD = "transactionDate"
            const val CARD_TRANSACTION_DEFAULT_SORT_ORDER = "DESC"

            const val WORKSHOP_DEFAULT_SORT_FIELD = "startDate"
            const val WORKSHOP_DEFAULT_SORT_ORDER = "DESC"
            const val WORKSHOP_MODIFY_DATE_SORT_FIELD = "modifyDate"
            const val WORKSHOP_NAME_SORT_FIELD = "name"
        }

        object File {
            const val QP_FILE_NAME = "fileName"
            const val QP_BUCKET_NAME = "bucketName"
            const val MULTIPART_PARAM_FILE = "file"
            const val MULTIPART_PARAM_PREVIOUS_FILE_NAME = "previousFileName"
            const val MULTIPART_PARAM_BUCKET_NAME = "bucketName"
        }

        object SWAGGER {
            const val QUERY_ENTITY_ID_DESC = "entity identifier"
            const val QUERY_PAGE_SIZE_DESC = "query page size, defaultValue=$DEFAULT_PAGE_SIZE"
            const val QUERY_PAGE_NUM_DESC = "query page num, defaultValue=$DEFAULT_PAGE_NUM"
            const val QUERY_PAGE_NUM_DEF_INT_VALUE = 1
            const val QUERY_PAGE_SIZE_DEF_INT_VALUE = 10
            const val QUERY_PAGE_NUM_DEF_VALUE = "$QUERY_PAGE_NUM_DEF_INT_VALUE"
            const val QUERY_PAGE_SIZE_DEF_VALUE = "$QUERY_PAGE_SIZE_DEF_INT_VALUE"
            const val DATETIME_FROM = "DateTime from in millis"
            const val DATETIME_TO = "DateTime to in millis"
        }

    }

    object Exceptions {
        const val EX_NOT_FOUND = "NotFound"
        const val EX_BAD_REQUEST = "BadRequest"
        const val EX_CONNECTION_RESET_BY_PEER = "Connection reset by peer"
    }

    val isLocalAddress = Predicate { testAddress: String -> LOCAL_ADDRESSES.contains(testAddress) }

}
