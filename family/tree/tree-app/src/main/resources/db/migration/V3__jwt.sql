
create table core_issued_jwt
(
    jwt_id         tidbigcode default nextval('seq_action_id') not null
        constraint pk_core_issued_jwt
            primary key,
    issue_date     tdatetime  not null,
    valid_until    tdatetime  not null,
    jwt            tstr2000   not null,
    issued_to      tstr2000   not null,
    tag            tstr2000,
    is_revoked     tboolean   not null,
    revoke_date    tdatetime
);

comment on table core_issued_jwt is 'Использованные access-токены';

alter table core_issued_jwt
    owner to dev_family_admin;

create index i_core_issued_jwt_jwt
    on core_issued_jwt (jwt);


create table core_refresh_jwt
(
    jwt_id        tidbigcode default nextval('seq_action_id') not null
        constraint pk_core_refresh_jwt
            primary key,
    issue_date    tdatetime  not null,
    jwt           tstr2000   not null,
    parent_jwt_id tidbigcode not null
        constraint fk_parent_jwt_id
            references core_issued_jwt
            on update restrict on delete restrict,
    valid_until   tdatetime  not null,
    is_revoked    tboolean   not null,
    revoke_date   tdatetime
);

alter table core_refresh_jwt
    owner to dev_family_admin;

create index i_core_refresh_jwt_jwt
    on core_refresh_jwt (jwt);


comment on table core_issued_jwt is 'Использованные refresh-токены';