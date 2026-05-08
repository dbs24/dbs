-- ROLE required only 4 testcontainers !!!
CREATE ROLE dev_cc_admin WITH LOGIN CREATEDB ENCRYPTED PASSWORD 'fakedPassword';

create sequence seq_action_id;

alter sequence seq_action_id owner to dev_cc_admin;

create domain tbinary as bytea;

alter domain tbinary owner to dev_cc_admin;

create domain tboolean as boolean;

alter domain tboolean owner to dev_cc_admin;

create domain tcurrencyiso as varchar(3);

alter domain tcurrencyiso owner to dev_cc_admin;

create domain tcurrencystr10 as varchar(10);

alter domain tcurrencystr10 owner to dev_cc_admin;

create domain tdate as date;

alter domain tdate owner to dev_cc_admin;

create domain tdatetime as timestamp;

alter domain tdatetime owner to dev_cc_admin;

create domain tidbigcode as bigint;

alter domain tidbigcode owner to dev_cc_admin;

create domain tidcode as integer;

comment on type tidcode is 'Код сущности';

alter domain tidcode owner to dev_cc_admin;

create domain tiduser as integer;

alter domain tiduser owner to dev_cc_admin;

create domain timage as bytea;

alter domain timage owner to dev_cc_admin;

create domain tintcounter as integer;

alter domain tintcounter owner to dev_cc_admin;

create domain tinteger as integer;

alter domain tinteger owner to dev_cc_admin;

create domain titemtype as smallint;

alter domain titemtype owner to dev_cc_admin;

create domain tmoney as numeric(22, 4);

alter domain tmoney owner to dev_cc_admin;

create domain tpercrate as numeric(12, 6);

alter domain tpercrate owner to dev_cc_admin;

create domain tpercrateext as numeric(16, 8);

alter domain tpercrateext owner to dev_cc_admin;

create domain treal as real;

alter domain treal owner to dev_cc_admin;

create domain tstr10 as varchar(10);

alter domain tstr10 owner to dev_cc_admin;

create domain tstr100 as varchar(100);

alter domain tstr100 owner to dev_cc_admin;

create domain tstr10000 as varchar(10000);

alter domain tstr10000 owner to dev_cc_admin;

create domain tstr128 as varchar(128);

alter domain tstr128 owner to dev_cc_admin;

create domain tstr2 as varchar(2);

alter domain tstr2 owner to dev_cc_admin;

create domain tstr20 as varchar(20);

alter domain tstr20 owner to dev_cc_admin;

create domain tstr200 as varchar(200);

alter domain tstr200 owner to dev_cc_admin;

create domain tstr2000 as varchar(2000);

alter domain tstr2000 owner to dev_cc_admin;

create domain tstr3 as varchar(3);

alter domain tstr3 owner to dev_cc_admin;

create domain tstr30 as varchar(30);

alter domain tstr30 owner to dev_cc_admin;

create domain tstr40 as varchar(40);

alter domain tstr40 owner to dev_cc_admin;

create domain tstr400 as varchar(400);

alter domain tstr400 owner to dev_cc_admin;

create domain tstr50 as varchar(50);

alter domain tstr50 owner to dev_cc_admin;

create domain tstr80 as varchar(80);

alter domain tstr80 owner to dev_cc_admin;

create domain tsumext as numeric(24, 4);

alter domain tsumext owner to dev_cc_admin;

create domain ttext as text;

alter domain ttext owner to dev_cc_admin;

create domain ttime as time;

alter domain ttime owner to dev_cc_admin;

create domain tidbytecode as numeric(1, 0);

alter domain tidbytecode owner to dev_cc_admin;

create domain tstr500 as varchar(500);

alter domain tstr500 owner to dev_cc_admin;

create domain tstr1000 as varchar(1000);

alter domain tstr1000 owner to dev_cc_admin;

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
    owner to dev_cc_admin;

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
    owner to dev_cc_admin;

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
    owner to dev_cc_admin;

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
    owner to dev_cc_admin;

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
    owner to dev_cc_admin;

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
    owner to dev_cc_admin;

create table cc_lobbies
(
    lobby_id   tidbigcode not null
        constraint fk_cc_lobbies_lobby_id
            references core_entities,
    owner_id   tidbigcode not null,
    lobby_name tstr200    not null,
    lobby_code tstr50     not null,
    lobby_kind tidcode    not null
);

alter table cc_lobbies
    owner to dev_cc_admin;

create table cc_players
(
    player_id         tidbigcode not null
        constraint pk_players
            primary key
        constraint fk_players_players_player_id
            references core_entities,
    player_login      tstr100    not null
        constraint ak_cc_players_player_login
            unique,
    password_hash     tstr200,
    first_name        tstr100,
    middle_name       tstr100,
    last_name         tstr100,
    gender            tstr2,
    birth_date        tdate,
    country           tstr3,
    avatar_path       tstr200,
    small_avatar_path tstr200,
    email             tstr200,
    phone             tstr100
);

alter table cc_players
    owner to dev_cc_admin;

create table cc_players_hist
(
    player_id         tidbigcode not null,
    actual_date       tdatetime  not null,
    player_login      tstr100,
    password_hash     tstr200,
    first_name        tstr100,
    middle_name       tstr100,
    last_name         tstr100,
    gender            tstr2,
    birth_date        tdate,
    country           tstr3,
    avatar_path       tstr200,
    small_avatar_path tstr200,
    email             tstr200,
    phone             tstr100
);

alter table cc_players_hist
    owner to dev_cc_admin;

create table cc_lobbies_hist
(
    lobby_id    tidbigcode,
    actual_fate tdatetime,
    owner_id    tidbigcode,
    lobby_name  tstr200,
    lobby_code  tstr50,
    lobby_kind  tidcode
);

alter table cc_lobbies_hist
    owner to dev_cc_admin;
