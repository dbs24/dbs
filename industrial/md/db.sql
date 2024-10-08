--====================================================
--jdbc:postgresql://dev01.k11dev.tech:5432/dev_goods_db?user=dev_goods_admin&password=15486002099248ee8378b0394
--r2dbc:postgresql://dev01.k11dev.tech:5432/dev_goods_db
--====================================================
--CREATE SCHEMA dev_goods_schema;
--ALTER DATABASE dev_goods_db SET search_path TO dev_goods_schema;
--SET search_path TO dev_goods_schema;
--====================================================

create sequence seq_action_id;

alter sequence seq_action_id owner to dev_goods_admin;

create domain tbinary as bytea;

alter domain tbinary owner to dev_goods_admin;

create domain tboolean as boolean;

alter domain tboolean owner to dev_goods_admin;

create domain tcurrencyiso as varchar(3);

alter domain tcurrencyiso owner to dev_goods_admin;

create domain tcurrencystr10 as varchar(10);

alter domain tcurrencystr10 owner to dev_goods_admin;

create domain tdate as date;

alter domain tdate owner to dev_goods_admin;

create domain tdatetime as timestamp;

alter domain tdatetime owner to dev_goods_admin;

create domain tidbigcode as bigint;

alter domain tidbigcode owner to dev_goods_admin;

create domain tidcode as integer;

comment on type tidcode is 'Код сущности';

alter domain tidcode owner to dev_goods_admin;

create domain tiduser as integer;

alter domain tiduser owner to dev_goods_admin;

create domain timage as bytea;

alter domain timage owner to dev_goods_admin;

create domain tintcounter as integer;

alter domain tintcounter owner to dev_goods_admin;

create domain tinteger as integer;

alter domain tinteger owner to dev_goods_admin;

create domain titemtype as smallint;

alter domain titemtype owner to dev_goods_admin;

create domain tmoney as numeric(22, 4);

alter domain tmoney owner to dev_goods_admin;

create domain tpercrate as numeric(12, 6);

alter domain tpercrate owner to dev_goods_admin;

create domain tpercrateext as numeric(16, 8);

alter domain tpercrateext owner to dev_goods_admin;

create domain treal as real;

alter domain treal owner to dev_goods_admin;

create domain tstr10 as varchar(10);

alter domain tstr10 owner to dev_goods_admin;

create domain tstr100 as varchar(100);

alter domain tstr100 owner to dev_goods_admin;

create domain tstr10000 as varchar(10000);

alter domain tstr10000 owner to dev_goods_admin;

create domain tstr128 as varchar(128);

alter domain tstr128 owner to dev_goods_admin;

create domain tstr2 as varchar(2);

alter domain tstr2 owner to dev_goods_admin;

create domain tstr20 as varchar(20);

alter domain tstr20 owner to dev_goods_admin;

create domain tstr200 as varchar(200);

alter domain tstr200 owner to dev_goods_admin;

create domain tstr2000 as varchar(2000);

alter domain tstr2000 owner to dev_goods_admin;

create domain tstr3 as varchar(3);

alter domain tstr3 owner to dev_goods_admin;

create domain tstr30 as varchar(30);

alter domain tstr30 owner to dev_goods_admin;

create domain tstr40 as varchar(40);

alter domain tstr40 owner to dev_goods_admin;

create domain tstr400 as varchar(400);

alter domain tstr400 owner to dev_goods_admin;

create domain tstr50 as varchar(50);

alter domain tstr50 owner to dev_goods_admin;

create domain tstr80 as varchar(80);

alter domain tstr80 owner to dev_goods_admin;

create domain tsumext as numeric(24, 4);

alter domain tsumext owner to dev_goods_admin;

create domain ttext as text;

alter domain ttext owner to dev_goods_admin;

create domain ttime as time;

alter domain ttime owner to dev_goods_admin;

create domain tidbytecode as numeric(1, 0);

alter domain tidbytecode owner to dev_goods_admin;

create domain tstr500 as varchar(500);

alter domain tstr500 owner to dev_goods_admin;

create domain tstr1000 as varchar(1000);

alter domain tstr1000 owner to dev_goods_admin;

create table if not exists core_action_codes_ref
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

create table if not exists core_entity_types_ref
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

create table if not exists core_entity_kinds_ref
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

create table if not exists core_entity_statuses_ref
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

create table if not exists core_entities
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

create table if not exists core_actions
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

create table if not exists product_category
(
    category_id        tidbigcode not null
    constraint pk_category
    primary key,
    parent_category_id tidcode,
    category_name      tstr100    not null,
    category_code      tstr100    not null,
    category_img_url   tstr1000
);

comment on table product_category is 'Категории продуктов';

comment on column product_category.category_id is 'код учетной категории';

comment on column product_category.parent_category_id is 'код родительской категории';

comment on column product_category.category_name is 'Наименование категории';

comment on column product_category.category_code is 'Код категории текстовый';

comment on column product_category.category_img_url is 'Ссылка на изображение';

alter table product_category
    owner to dev_goods_admin;

create table if not exists product_catalog
(
    product_id    tidbigcode not null
    constraint pk_product_catalog
    primary key
    constraint fk_product_id
    references core_entities,
    product_code  tstr100    not null,
    product_name  tstr200    not null,
    category_id   tidbigcode
    constraint fk_product_catalog
    references product_category,
    warranty_days tinteger   not null
);

comment on table product_catalog is 'Каталог номенклатуры продукции';

comment on column product_catalog.product_id is 'Код товара';

comment on constraint fk_product_id on product_catalog is 'Код изделия';

comment on column product_catalog.product_code is 'Код товара в номенклатуре';

comment on column product_catalog.product_name is 'Наимнование товара';

comment on column product_catalog.category_id is 'Категория товара';

comment on column product_catalog.warranty_days is 'Срок службы, дней';

alter table product_catalog
    owner to dev_goods_admin;

create table if not exists product_statuses
(
    product_status_id   tidbigcode not null
    constraint product_statuses_pk
    primary key
    constraint fk_product_statuses_status_id
    references core_entities,
    product_id          tidbigcode not null
    constraint fk_product_statuses_product_id
    references core_entities,
    product_status_name tidbigcode not null
);

comment on table product_statuses is 'Статусы продукта';

comment on column product_statuses.product_status_id is 'Код статуса';

comment on column product_statuses.product_id is 'Код продукта';

comment on column product_statuses.product_status_name is 'Наименование статуса продукта';

alter table product_statuses
    owner to dev_goods_admin;

create table if not exists product_allowed_routes
(
    route_id           tidbigcode not null
    constraint pk_product_routes_product_id
    primary key,
    product_id         tidbigcode not null
    constraint fk_product_routes
    references product_catalog,
    route_name         tstr200    not null,
    product_status_in  tidbigcode not null
    constraint fk_product_status_in
    references product_statuses,
    product_status_out tidbigcode not null
    constraint fk_product_status_out
    references product_statuses
);

comment on table product_allowed_routes is 'Допустимые переходы продукта (изделия) из статуса в статус';

comment on column product_allowed_routes.route_id is 'Код перехода из статуса в статус';

comment on column product_allowed_routes.product_id is 'код продукта';

comment on column product_allowed_routes.route_name is 'Наименования маршрута (действия)';

comment on column product_allowed_routes.product_status_in is 'Код входящего статуса изделия';

comment on column product_allowed_routes.product_status_out is 'Код исходящего статуса изделия';

alter table product_allowed_routes
    owner to dev_goods_admin;

create table if not exists product_instances
(
    instance_id         tidbigcode not null
    constraint pk_product_instances
    primary key,
    product_status_id   tidbigcode not null
    constraint fk_product_instances
    references product_statuses,
    product_category_id tidbigcode not null
    constraint fk_product_instances_category_id
    references product_category,
    create_date         tdatetime  not null,
    sale_date           tdatetime,
    warranty_date       tdatetime,
    note                tstr10000,
    qr                  bytea,
    product_images      tstr10000,
    serial_number       tstr200
);

comment on table product_instances is 'Готовые экземпляры (изделия) продукта';

comment on column product_instances.instance_id is 'внутренний код экземляра изделия';

comment on column product_instances.product_status_id is 'код статуса изделия';

comment on column product_instances.product_category_id is 'код категории продукта';

comment on column product_instances.create_date is 'Дата создания изделия';

comment on column product_instances.sale_date is 'Дата продажи изделия';

comment on column product_instances.warranty_date is 'Дата окончания гарантии ';

comment on column product_instances.note is 'примечание';

comment on column product_instances.qr is 'qr код';

comment on column product_instances.product_images is 'ссылки на изображения продукта';

comment on column product_instances.serial_number is 'серийный номер изделия';

alter table product_instances
    owner to dev_goods_admin;

create table if not exists product_instances_actions
(
    product_action_id   tidbigcode not null
    constraint pk_product_instances_actions
    primary key,
    route_id            integer    not null
    constraint fk_product_instances_actions_route_id
    references product_allowed_routes,
    user_id             tidbigcode not null,
    product_instance_id tidbigcode not null
    constraint fk_product_instances_actions
    references product_instances,
    action_date         tdatetime  not null,
    address             tstr100,
    action_note         tstr1000
);

comment on table product_instances_actions is 'Действия над экземплярами продукта';

comment on column product_instances_actions.product_action_id is 'Код действия';

comment on column product_instances_actions.route_id is 'Код маршрута';

comment on column product_instances_actions.user_id is 'Код пользователя выполнившего действия';

comment on column product_instances_actions.product_instance_id is 'Код экземляра продукта';

comment on column product_instances_actions.action_date is 'Дата действия';

comment on column product_instances_actions.address is 'c какого адреса выполнено';

comment on column product_instances_actions.action_note is 'Прнимечание к действию';

alter table product_instances_actions
    owner to dev_goods_admin;

