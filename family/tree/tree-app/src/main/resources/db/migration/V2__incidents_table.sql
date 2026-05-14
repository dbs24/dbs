
CREATE TABLE core_incidents (
                                incident_id           VARCHAR  PRIMARY KEY,
                                source                VARCHAR NOT NULL,
                                create_date           TIMESTAMP    NOT NULL,
                                stack_trace           TEXT         NOT NULL,
                                os_open_files         BIGINT       NOT NULL,
                                jvm_free_memory_bytes BIGINT       NOT NULL,
                                jvm_total_memory_bytes BIGINT       NOT NULL,
                                jvm_max_memory_bytes  BIGINT       NOT NULL
);

CREATE INDEX idx_core_incidents_create_date ON core_incidents (create_date);