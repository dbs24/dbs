-- ROLE required only 4 testcontainers !!!
CREATE ROLE dev_goods_admin WITH LOGIN CREATEDB ENCRYPTED PASSWORD 'fakedPassword';

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
    owner to dev_goods_admin;

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
    owner to dev_goods_admin;

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
    owner to dev_goods_admin;

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
    owner to dev_goods_admin;

create table core_entities
(
    entity_id        tidbigcode not null
        constraint pk_core_entities
            primary key,
    entity_type_id   tidcode    not null
        constraint fk_core_ent_entity_ty_core_ent
            references core_entity_types_ref
            on update restrict on delete restrict,
    entity_status_id tidcode    not null
        constraint fk_core_entities_status_id
            references core_entity_statuses_ref,
    create_date      tdatetime  not null,
    close_date       tdatetime,
    modify_date      tdatetime
);

comment on table core_entities is 'Картотека сущностей';

alter table core_entities
    owner to dev_goods_admin;

create table core_actions
(
    action_id       tidbigcode not null
        constraint pk_core_actions
            primary key,
    entity_id       tidbigcode not null
        constraint fk_actions_entity_id
            references core_entities
            on update restrict on delete restrict,
    action_code     tidcode    not null
        constraint fk_ta_actrefid
            references core_action_codes_ref
            on update restrict on delete restrict,
    user_id         tidbigcode not null,
    execute_date    tdatetime  not null,
    action_address  tstr100    not null,
    err_msg         ttext,
    action_duration ttime,
    notes           ttext
);

comment on table core_actions is 'Картотека выполненный действий пользователя
';

alter table core_actions
    owner to dev_goods_admin;

create table product_attributes_def
(
    attr_id           tidbigcode not null
        constraint pk_product_attributes_def
            primary key
        constraint fk_product_attributes_def_id
            references core_entities,
    attr_code         tstr100    not null
        constraint ak_product_attributes_def
            unique,
    attr_name         tstr200    not null,
    attr_value_regexp tstr200,
    attr_desc         tstr1000
);

comment on table product_attributes_def is 'Аттрибуты продуктов';

comment on column product_attributes_def.attr_id is 'id';

comment on column product_attributes_def.attr_code is 'код';

alter table product_attributes_def
    owner to dev_goods_admin;

create table product_templates
(
    template_id         tidbigcode not null
        constraint pk_product_template
            primary key
        constraint fk_product_template_id
            references core_entities,
    template_code       tstr100    not null
        constraint ak_product_template
            unique,
    template_name       tstr200    not null,
    product_description tstr1000
);

comment on table product_templates is 'Шаблоны продуктов';

alter table product_templates
    owner to dev_goods_admin;

create table product_template_attrs
(
    template_attr_id tidbigcode not null
        constraint pk_product_template_attrs
            primary key
        constraint fk_product_template_attrs_id
            references core_entities,
    attr_id          tidbigcode not null
        constraint fk_product_template_attr_id
            references product_attributes_def,
    template_id      tidbigcode not null
        constraint fk_product_template_attrs_template_id
            references product_templates,
    attr_value       tstr1000   not null
);

comment on table product_template_attrs is 'Атрибуты шаблона продукта';

alter table product_template_attrs
    owner to dev_goods_admin;

create table products
(
    product_id  tidbigcode not null
        constraint pk_products
            primary key
        constraint fk_products_id
            references core_entities,
    template_id tidbigcode not null
        constraint fk_products_template_id
            references product_templates,
    serial_num  tstr100
);

alter table products
    owner to dev_goods_admin;

create table users
(
    user_id       tidbigcode not null
        constraint pk_product_users
            primary key
        constraint fk_product_users_id
            references core_entities,
    user_login    tstr100    not null
        constraint ak_product_users
            unique,
    first_name    tstr100    not null,
    last_name     tstr100    not null,
    email         tstr100,
    password_hash tstr1000
);

alter table users
    owner to dev_goods_admin;

create table user_privileges_ref
(
    privilege_id   tidcode not null
        constraint pk_core_functions_ref
            primary key,
    privilege_code tstr30  not null,
    privilege_name tstr100 not null
);

alter table user_privileges_ref
    owner to dev_goods_admin;

create table user_privileges
(
    privilege_id tidcode    not null
        constraint fk_user_privileges_privilege_id
            references user_privileges_ref
            on update restrict on delete restrict,
    user_id      tidbigcode not null
        constraint fk_user_privileges_id
            references users
            on update restrict on delete restrict,
    assign_date  tdatetime  not null,
    revoke_date  tdatetime,
    assign_id    tidbigcode not null
        constraint pk_user_privileges
            primary key
        constraint fk_user_privileges_assign_id
            references core_entities,
    constraint ak_user_privileges
        unique (user_id, privilege_id)
);

alter table user_privileges
    owner to dev_goods_admin;
