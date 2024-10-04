package org.dbs.consts

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

typealias AccountId = EntityId
typealias AccountNum = String
typealias AccountName = String
typealias AccountRestId = EntityId
typealias RestAmount = Money

typealias TransactionKindId = Int
typealias TransactionKindName = String
typealias TransactionKindCode = String

typealias TransactionStatusId = Int
typealias TransactionStatusName = String
typealias TransactionStatusCode = String

typealias Address = String
typealias ApplicationName = String
typealias BirthDate = LocalDate
typealias BirthDateDto = Int
typealias BirthDayIntNull = BirthDateDto?
typealias BooleanNull = Boolean?
typealias Login = String
typealias Password = String
typealias PasswordHash = Password
typealias PasswordNull = Password?
typealias AnyCode = String
typealias AnyCodeNull = AnyCode?

typealias CardNum = AnyCode
typealias CountryIsoCode = AnyCode
typealias CurrencyId = Int
typealias CurrencyIso = String
typealias CurrencyName = String
typealias CurrencyCode = String
typealias CountryCodeNull = CountryIsoCode?
typealias CustomerId = EntityId

typealias CurrencyCodeNull = String?

typealias DocumentFile = String
typealias DocumentFileNull = String?
typealias DocumentNumber = String
typealias Email = String
typealias EmailNull = String?
typealias ErrMsg = String

typealias EntityId = Long
typealias ActionId = EntityId
typealias EntityIdNull = EntityId?
typealias EntityKindId = Int
typealias EntityKindCode = String
typealias EntityKindName = String
typealias EntityTypeId = Int
typealias EntityTypeName = String
typealias EntityCode = String
typealias EntityCodeNull = String?
typealias EntityStatusId = Int
typealias EntityStatusIdNull = EntityStatusId?
typealias EntityStatusName = String
typealias EntityStatusShortName = EntityStatusName
typealias EntityName = String
typealias EntityPrioritySortingId = Int
typealias IpAddress = String

typealias CompanyName = String
typealias CompanyNameNull = String?

typealias TopicId = Int
typealias TopicName = String
typealias TopicCode = String

typealias ManufactureName = String
typealias ShortManufactureName = String
typealias ManufactureCode = String

typealias PaymentSystemId = Int
typealias ProductSku = EntityCode
typealias ProductSkuNull = ProductSku?
typealias ProductName = String
typealias ProductDocuments = String
typealias ProductDocumentName = String
typealias ProductsFullName = String
typealias ProductImageName = String
typealias ProductImage = String
typealias ProductsImages = String
typealias ProductsImagesNull = String?
typealias DisputeImage = String
typealias DisputeImageName = String
typealias DisputesImages = String
typealias DisputesImagesNull = String?
typealias MapKey = String
typealias ManagerAvatarImage = String
typealias ManagerAvatarImageName = String
typealias CategoryName = String
typealias VendorLogin = EntityCode
typealias Phone = String
typealias PhoneNull = Phone?
typealias ReferenceId = Int
typealias ReferenceName = String
typealias StringNote = String
typealias StringEntityStatus = String
typealias StringEntityStatusNull = String?
typealias StringNoteNull = String?
typealias StringEmail = String
typealias StringEmailNull = String?
typealias StringAddress = String
typealias StringAddressNull = String?
typealias StringDocNum = String
typealias StringDocNumNull = String?
typealias StringUrl = String
typealias StringUrlNull = StringUrl?

typealias DefaultMessageValue = String
typealias Money = BigDecimal
typealias MoneyNull = BigDecimal?

typealias RejectReason = String
typealias RejectReasonNullable = RejectReason?
typealias RestDate = LocalDate
typealias RestDateTime = LocalDateTime
typealias RestDateDto = OperDateDto
typealias RestDateTimeDto = OperDateDto
typealias OperDate = LocalDateTime
typealias OperDateString = String
typealias OperDateNull = LocalDateTime?
typealias TermDate = LocalDateTime
typealias TermDateNull = LocalDateTime?
typealias ClosedStatus = Boolean
typealias DateTimeLong = Long
typealias DateTimeDto = DateTimeLong
typealias OperDateDto = Long
typealias OperDateDtoNull = Long?
typealias ReferenceCode = String
typealias RegionCode = String
typealias RegionCodeNull = RegionCode?
// Lms Status
typealias StatusId = String
typealias StatusName = String
typealias StatusCode = String
// Roles
typealias RoleId = Long
typealias RoleIdNull = RoleId?
typealias PrivilegeId = Int
typealias PrivilegeCode = AnyCode
typealias PrivilegeName = String
typealias PrivilegeIdNull = PrivilegeId?
typealias RootPrivilege = Boolean
typealias PrivilegeGroupId = Int
typealias PrivilegeGroupCode = AnyCode
// Jvm
typealias JavaLong = java.lang.Long
// Security
typealias JwtId = Long
typealias Jwt = String
typealias JwtNull = Jwt?
// funcs
typealias NoArg2Unit = () -> Unit
typealias GenericArg2Unit<T> = (T) -> Unit
typealias SuspendGenericArg2Unit<T> = suspend (T) -> Unit
typealias String2Unit = GenericArg2Unit<String>
typealias Money2Unit = GenericArg2Unit<Money>
typealias Int2Unit = GenericArg2Unit<Int>
typealias Long2Unit = GenericArg2Unit<Long>
typealias NoArg2Generic<T> = () -> T
typealias SuspendNoArg2Generic<T> = suspend () -> T
typealias Arg2Generic<K, T> = (K) -> T
typealias SuspendArg2Generic<K, T> = suspend (K) -> T
// collections
typealias Collection2Unit<T> = (Collection<T>) -> Unit
// maps
typealias StringMap = Map<String, String>
// pairs
typealias StringToBoolean = Pair<String, Boolean>

// funcs
typealias NoArg2String = NoArg2Generic<String>

// http
typealias UriPath = String
typealias QueryParamName = String
typealias QueryParamNullable = String?
typealias QueryParamString = String
typealias RouteUrl = String

typealias TaskId = EntityId
typealias suspendNoArg = suspend () -> Unit
