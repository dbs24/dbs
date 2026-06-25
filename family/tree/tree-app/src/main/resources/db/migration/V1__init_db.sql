-- ROLE required only 4 testcontainers !!!
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'dev_family_admin') THEN
   CREATE ROLE dev_family_admin WITH LOGIN CREATEDB ENCRYPTED PASSWORD 'fakedPassword';
END IF;
END
$$;

create sequence seq_action_id CACHE 20;

alter sequence seq_action_id owner to dev_family_admin;

create domain tbinary as bytea;

alter domain tbinary owner to dev_family_admin;

create domain tboolean as boolean;

alter domain tboolean owner to dev_family_admin;

create domain tcurrencyiso as varchar(3);

alter domain tcurrencyiso owner to dev_family_admin;

create domain tcurrencystr10 as varchar(10);

alter domain tcurrencystr10 owner to dev_family_admin;

create domain tdate as date;

alter domain tdate owner to dev_family_admin;

create domain tdatetime as timestamp;

alter domain tdatetime owner to dev_family_admin;

create domain tidbigcode as bigint;

alter domain tidbigcode owner to dev_family_admin;

create domain tidcode as integer;

comment on type tidcode is 'Код сущности';

alter domain tidcode owner to dev_family_admin;

create domain tiduser as integer;

alter domain tiduser owner to dev_family_admin;

create domain timage as bytea;

alter domain timage owner to dev_family_admin;

create domain tintcounter as integer;

alter domain tintcounter owner to dev_family_admin;

create domain tinteger as integer;

alter domain tinteger owner to dev_family_admin;

create domain titemtype as smallint;

alter domain titemtype owner to dev_family_admin;

create domain tmoney as numeric(22, 4);

alter domain tmoney owner to dev_family_admin;

create domain tpercrate as numeric(12, 6);

alter domain tpercrate owner to dev_family_admin;

create domain tpercrateext as numeric(16, 8);

alter domain tpercrateext owner to dev_family_admin;

create domain treal as real;

alter domain treal owner to dev_family_admin;

create domain tstr10 as varchar(10);

alter domain tstr10 owner to dev_family_admin;

create domain tstr100 as varchar(100);

alter domain tstr100 owner to dev_family_admin;

create domain tstr10000 as varchar(10000);

alter domain tstr10000 owner to dev_family_admin;

create domain tstr128 as varchar(128);

alter domain tstr128 owner to dev_family_admin;

create domain tstr2 as varchar(2);

alter domain tstr2 owner to dev_family_admin;

create domain tstr20 as varchar(20);

alter domain tstr20 owner to dev_family_admin;

create domain tstr200 as varchar(200);

alter domain tstr200 owner to dev_family_admin;

create domain tstr2000 as varchar(2000);

alter domain tstr2000 owner to dev_family_admin;

create domain tstr3 as varchar(3);

alter domain tstr3 owner to dev_family_admin;

create domain tstr30 as varchar(30);

alter domain tstr30 owner to dev_family_admin;

create domain tstr40 as varchar(40);

alter domain tstr40 owner to dev_family_admin;

create domain tstr400 as varchar(400);

alter domain tstr400 owner to dev_family_admin;

create domain tstr50 as varchar(50);

alter domain tstr50 owner to dev_family_admin;

create domain tstr80 as varchar(80);

alter domain tstr80 owner to dev_family_admin;

create domain tsumext as numeric(24, 4);

alter domain tsumext owner to dev_family_admin;

create domain ttext as text;

alter domain ttext owner to dev_family_admin;

create domain ttime as time;

alter domain ttime owner to dev_family_admin;

create domain tidbytecode as numeric(1, 0);

alter domain tidbytecode owner to dev_family_admin;

create domain tstr500 as varchar(500);

alter domain tstr500 owner to dev_family_admin;

create domain tstr1000 as varchar(1000);

alter domain tstr1000 owner to dev_family_admin;

create table core_action_codes_ref
(
    action_code tidcode  not null
        constraint pk_core_action_codes_ref
            primary key,
    action_name tstr80,
    app_name    tstr100  not null,
    is_closed   tboolean not null
);

comment on table core_action_codes_ref is 'Справочник зарегистрированных регистрированных действий';

alter table core_action_codes_ref
    owner to dev_family_admin;

create table core_entity_types_ref
(
    entity_type_id   tidcode not null
        constraint pk_core_entity_types_ref
            primary key,
    entity_type_name tstr100 not null,
    entity_app_name  tstr100 not null
);

comment on table core_entity_types_ref is 'Cправочник сущностей';

alter table core_entity_types_ref
    owner to dev_family_admin;

create table core_entity_kinds_ref
(
    entity_kind_id   tidcode not null
        constraint pk_core_entity_kinds_ref
            primary key,
    entity_type_id   tidcode not null
        constraint fk_core_ent_entity_ki_core_ent
            references core_entity_types_ref
            on update restrict on delete restrict,
    entity_kind_name tstr100 not null
);

comment on table core_entity_kinds_ref is 'Cправочник видов сущностей';

alter table core_entity_kinds_ref
    owner to dev_family_admin;

create table core_entity_statuses_ref
(
    entity_status_id   tidcode not null
        constraint pk_core_r2dbc_entity_statuses_ref
            primary key,
    entity_type_id     tidcode not null
        constraint fk_core_ent_entity_ty_core_ent
            references core_entity_types_ref
            on update restrict on delete restrict,
    entity_status_name tstr50  not null
);

comment on table core_entity_statuses_ref is 'Справочник статусов ';

alter table core_entity_statuses_ref
    owner to dev_family_admin;

create table core_actions
(
    action_id       tidbigcode default nextval('seq_action_id') not null
        constraint pk_core_actions
            primary key,
    entity_id       tidbigcode                                            not null,
    action_code     tidcode                                               not null
        constraint fk_ta_actrefid
            references core_action_codes_ref
            on update restrict on delete restrict,
    user_id         tidbigcode                                            not null,
    execute_date    tdatetime                                             not null,
    action_address  tstr100                                               not null,
    err_msg         ttext,
    action_duration ttime,
    notes           ttext,
    entity_type_id  tidcode                                               not null
        constraint fk_core_actions_entity_type_id
            references core_entity_types_ref
            on update restrict on delete restrict
);

comment on table core_actions is 'Картотека выполненный действий пользователя';

alter table core_actions
    owner to dev_family_admin;


create table ft_users
(
    user_id         tidbigcode default nextval('seq_action_id') not null
        constraint pk_ft_users
            primary key,
    user_login      tstr100                                               not null
        constraint ak_ft_users_user_login
            unique,
    password_hash     tstr200,
    birth_date        tdate,
    first_name        tstr100,
    middle_name       tstr100,
    last_name         tstr100,
    email             tstr200,
    phone             tstr100,
    status_id         tidcode                                               not null
        constraint fk_ft_users_status_id
            references core_entity_statuses_ref
            on update restrict on delete restrict,
    create_date       tdatetime                                             not null,
    modify_date       tdatetime                                             not null,
    close_date        tdatetime
);

alter table ft_users
    owner to dev_family_admin;

alter table core_actions
    add constraint fk_core_actions_user_id foreign key (user_id)
        references ft_users on update restrict on delete restrict;


CREATE TABLE core_incidents (
                                incident_id           VARCHAR  PRIMARY KEY,
                                source                VARCHAR NOT NULL,
                                path                  VARCHAR NOT NULL,
                                create_date           TIMESTAMP    NOT NULL,
                                stack_trace           TEXT         NOT NULL,
                                os_open_files         BIGINT       NOT NULL,
                                jvm_free_memory_bytes BIGINT       NOT NULL,
                                jvm_total_memory_bytes BIGINT       NOT NULL,
                                jvm_max_memory_bytes  BIGINT       NOT NULL
);

comment on table core_incidents is 'Картотека регистрированных инцидентов (исключений)';

CREATE INDEX idx_core_incidents_create_date ON core_incidents (create_date);
