package org.dbs.kafka.consts

import org.dbs.kafka.consts.KafkaConsts.Topics.EMAIL_TOPIC
import org.dbs.kafka.consts.KafkaConsts.Topics.HTTP_REGISTRY_TOPIC
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_ACTORS_DEVICES_TOPIC
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_ACTOR_USER_TOPIC
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_BAD_QUESTION_4_ANALYSIS
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_BAD_QUESTION_ANALYSIS_RESP
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_EARTHQUAKE
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_MANAGER_LOGIN
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_NOTIFICATIONS_TOPIC
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_OFFER_SEND
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_PAYMENT
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_PAYMENT_ORDER_SEND
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_PAYMENT_ORDER_UPDATE
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_PRODUCT_PRICE_SEND
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_PRODUCT_SEND
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_PRODUCT_TO_STORE_SEND
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_UCI_SOLUTION
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_UCI_TASKER
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_VENDOR_SEND
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_WAREHOUSE_SEND

enum class KafkaTopicEnum(val topic: KafkaTopicName) {
    EMAIL(EMAIL_TOPIC), // NotificationDto
    PAYMENT_ORDER_SEND(KAFKA_PAYMENT_ORDER_SEND), // PaymentOrderDto
    PAYMENT_ORDER_UPDATE(KAFKA_PAYMENT_ORDER_UPDATE), // ConfirmPaymentDto
    PAYMENT_CREATE(KAFKA_PAYMENT), // PaymentDto
    HTTP_REGISTRY(HTTP_REGISTRY_TOPIC),
    WAREHOUSE_SEND(KAFKA_WAREHOUSE_SEND), // WarehouseDto
    PRODUCT_SEND(KAFKA_PRODUCT_SEND), // ProductDto
    PRODUCT_TO_STORE_SEND(KAFKA_PRODUCT_TO_STORE_SEND), // ProductStoreDto
    PRODUCT_PRICE_SEND(KAFKA_PRODUCT_PRICE_SEND), // ProductPricePCDto
    OFFER_SEND(KAFKA_OFFER_SEND), // OfferPCDto
    VENDOR_SEND(KAFKA_VENDOR_SEND), // VendorPCDto
    REVOKED_MANAGER_LOGIN_SEND(KAFKA_MANAGER_LOGIN), // Revoked Manager

    // LMS Moodle
    ACTOR_USER_SEND(KAFKA_ACTOR_USER_TOPIC),
    BAD_QUESTION_4_ANAYSIS_SEND(KAFKA_BAD_QUESTION_4_ANALYSIS), // Do analysis for wuestion with bad answers
    BAD_QUESTION_ANALYSIS_SEND(KAFKA_BAD_QUESTION_ANALYSIS_RESP),
    EARTHQUAKE(KAFKA_EARTHQUAKE),
    ACTOR_DEVICE(KAFKA_ACTORS_DEVICES_TOPIC),
    NOTIFICATION(KAFKA_NOTIFICATIONS_TOPIC),
    // CM
    CM_SEND_TASK(KAFKA_UCI_TASKER),
    CM_SEND_SOLUTION(KAFKA_UCI_SOLUTION)
}
