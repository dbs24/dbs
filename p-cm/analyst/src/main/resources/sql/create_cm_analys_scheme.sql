-- ROLE required only 4 testcontainers !!!
CREATE ROLE dev_cm_analyst_admin WITH LOGIN CREATEDB ENCRYPTED PASSWORD 'fakedPassword';

create sequence seq_action_id;

alter sequence seq_action_id owner to dev_cm_analyst_admin;

create domain tbinary as bytea;

alter domain tbinary owner to dev_cm_analyst_admin;

create domain tboolean as boolean;

alter domain tboolean owner to dev_cm_analyst_admin;

create domain tcurrencyiso as varchar(3);

alter domain tcurrencyiso owner to dev_cm_analyst_admin;

create domain tcurrencystr10 as varchar(10);

alter domain tcurrencystr10 owner to dev_cm_analyst_admin;

create domain tdate as date;

alter domain tdate owner to dev_cm_analyst_admin;

create domain tdatetime as timestamp;

alter domain tdatetime owner to dev_cm_analyst_admin;

create domain tidbigcode as bigint;

alter domain tidbigcode owner to dev_cm_analyst_admin;

create domain tidcode as integer;

comment on type tidcode is 'Код сущности';

alter domain tidcode owner to dev_cm_analyst_admin;

create domain tiduser as integer;

alter domain tiduser owner to dev_cm_analyst_admin;

create domain timage as bytea;

alter domain timage owner to dev_cm_analyst_admin;

create domain tintcounter as integer;

alter domain tintcounter owner to dev_cm_analyst_admin;

create domain tinteger as integer;

alter domain tinteger owner to dev_cm_analyst_admin;

create domain titemtype as smallint;

alter domain titemtype owner to dev_cm_analyst_admin;

create domain tmoney as numeric(22, 4);

alter domain tmoney owner to dev_cm_analyst_admin;

create domain tpercrate as numeric(12, 6);

alter domain tpercrate owner to dev_cm_analyst_admin;

create domain tpercrateext as numeric(16, 8);

alter domain tpercrateext owner to dev_cm_analyst_admin;

create domain treal as real;

alter domain treal owner to dev_cm_analyst_admin;

create domain tstr10 as varchar(10);

alter domain tstr10 owner to dev_cm_analyst_admin;

create domain tstr100 as varchar(100);

alter domain tstr100 owner to dev_cm_analyst_admin;

create domain tstr10000 as varchar(10000);

alter domain tstr10000 owner to dev_cm_analyst_admin;

create domain tstr128 as varchar(128);

alter domain tstr128 owner to dev_cm_analyst_admin;

create domain tstr2 as varchar(2);

alter domain tstr2 owner to dev_cm_analyst_admin;

create domain tstr20 as varchar(20);

alter domain tstr20 owner to dev_cm_analyst_admin;

create domain tstr200 as varchar(200);

alter domain tstr200 owner to dev_cm_analyst_admin;

create domain tstr2000 as varchar(2000);

alter domain tstr2000 owner to dev_cm_analyst_admin;

create domain tstr3 as varchar(3);

alter domain tstr3 owner to dev_cm_analyst_admin;

create domain tstr30 as varchar(30);

alter domain tstr30 owner to dev_cm_analyst_admin;

create domain tstr40 as varchar(40);

alter domain tstr40 owner to dev_cm_analyst_admin;

create domain tstr400 as varchar(400);

alter domain tstr400 owner to dev_cm_analyst_admin;

create domain tstr50 as varchar(50);

alter domain tstr50 owner to dev_cm_analyst_admin;

create domain tstr80 as varchar(80);

alter domain tstr80 owner to dev_cm_analyst_admin;

create domain tsumext as numeric(24, 4);

alter domain tsumext owner to dev_cm_analyst_admin;

create domain ttext as text;

alter domain ttext owner to dev_cm_analyst_admin;

create domain ttime as time;

alter domain ttime owner to dev_cm_analyst_admin;

create domain tidbytecode as numeric(1, 0);

alter domain tidbytecode owner to dev_cm_analyst_admin;

create domain tstr500 as varchar(500);

alter domain tstr500 owner to dev_cm_analyst_admin;

create domain tstr1000 as varchar(1000);

alter domain tstr1000 owner to dev_cm_analyst_admin;

create table player_statuses_ref
(
    player_status_id   tidcode not null
        constraint player_statuses_ref_pk
            primary key,
    player_status_name tstr100 not null
);

alter table player_statuses_ref
    owner to dev_cm_analyst_admin;

create table player
(
    player_id        tidbigcode not null
        constraint player_pk
            primary key,
    player_status_id tidcode    not null
        constraint player_fk_player_status_id
            references player_statuses_ref,
    player_login     tstr20     not null
        constraint player_pk_player_login
            unique,
    first_name       tstr100    not null,
    last_name        tstr100    not null,
    token            tstr100    not null
        constraint player_pk_token
            unique,
    email            tstr100    not null
        constraint player_pk_email
            unique,
    password_hash    tstr200    not null,
    phone            tstr40
);

alter table player
    owner to dev_cm_analyst_admin;

create table solution_statuses_ref
(
    solution_status_id   tidcode not null
        constraint solution_statuses_ref_pk
            primary key,
    solution_status_name tstr100 not null
);

alter table solution_statuses_ref
    owner to dev_cm_analyst_admin;

create table solution
(
    solution_id        tidbigcode not null
        constraint solution_pk
            primary key,
    solution_status_id tidcode    not null
        constraint solution_fk_status_id
            references solution_statuses_ref,
    fen                tstr200    not null,
    depth              tinteger   not null,
    timeout            tinteger   not null,
    white_move         tboolean   not null,
    move               tstr10     not null,
    player_id          tidbigcode not null
        constraint solution_fk_player_id
            references player
);

alter table solution
    owner to dev_cm_analyst_admin;

create index solution_index_player_id
    on solution (solution_id desc);
