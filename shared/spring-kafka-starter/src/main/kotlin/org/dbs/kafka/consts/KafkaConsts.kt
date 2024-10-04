package org.dbs.kafka.consts

typealias KafkaTopicName = String

object KafkaConsts {
    const val TP_FAKED = "topic_faked"
    const val KAFKA_BEAN_NAME = "kafkaService"

    object Topics {
        const val KAFKA_PAYMENT_ORDER_SEND = "kt_payment_order_send_dev08"
        const val KAFKA_PAYMENT_ORDER_UPDATE = "kt_payment_order_update_dev08"
        const val KAFKA_PAYMENT = "kt_payment_dev01"
        const val KAFKA_WAREHOUSE_SEND = "kt_warehouse_send_dev02"
        const val KAFKA_PRODUCT_SEND = "kt_product_send_dev02"
        const val KAFKA_PRODUCT_PRICE_SEND = "kt_product_price_send_dev02"
        const val KAFKA_VENDOR_SEND = "kt_vendor_send_dev02"
        const val KAFKA_OFFER_SEND = "kt_offer_send_dev02"
        const val KAFKA_PRODUCT_TO_STORE_SEND = "kt_product_to_store_send_dev02"
        const val EMAIL_TOPIC = "kt_email_topic_dev02"
        const val HTTP_REGISTRY_TOPIC = "kt_registry_request_dev08"
        const val KAFKA_MEDIA_FILE_TOPIC = "kt_media_file_send_send_dev01"
        const val KAFKA_MANAGER_LOGIN = "kt_manager_login_send_dev01"
        const val KAFKA_BAD_QUESTION_4_ANALYSIS = "kt_bad_question_4_analysis_dev01"
        const val KAFKA_BAD_QUESTION_ANALYSIS_RESP = "kt_bad_question_analysis_resp_dev01"
        const val KAFKA_ACTOR_USER_TOPIC = "kt_actor_user_dev01"
        const val KAFKA_ACTOR_SEND = "kt_actor_send_v01"
        const val KAFKA_UCI_TASKER = "kt_uci_task_receiver_v02"
        const val KAFKA_UCI_SOLUTION = "kt_uci_solution_receiver_v01"
        const val KAFKA_EARTHQUAKE = "kt_earthquake_receive_message_dev01"
        const val KAFKA_ACTORS_DEVICES_TOPIC = "kt_actors_devices_dev01"
        const val KAFKA_NOTIFICATIONS_TOPIC = "kt_notifications_dev01"
        const val KAFKA_REVOKE_JWT = "kt_jwt_revoke_dev01"
    }

    object Groups {
        const val WAREHOUSE_GROUP_ID = "warehouses_groups"
        const val PAYMENT_GROUP_ID = "payments_groups"
        const val PRODUCT_GROUP_ID = "products_groups"
        const val PRODUCT_PRICE_GROUP_ID = "product_prices_groups"
        const val OFFER_GROUP_ID = "offers_groups"
        const val VENDOR_GROUP_ID = "vendors_groups"
        const val MANAGER_GROUP_ID = "manager_groups"
        const val ANALYSIS_BAD_QUESTION_ID = "analysis_bad_question_groups"
        const val ACTOR_USER_GROUP_ID = "actor_user_group"
        const val CM_GROUP_ID = "cm_user_group"
        const val ACTOR_DEVICE_GROUP_ID = "actor_device_group"
        const val NOTIFICATION_GROUP_ID = "notification_group"
        const val JWT_GROUP_ID = "jwt_groups"
    }
}
