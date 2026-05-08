-- V3: Add PK/FK/AK/UK constraints; drop core_entities table.
--     Completes the entity core refactoring started in V2.

-- core_entity_kinds_ref: add unique key on entity_kind_name
alter table core_entity_kinds_ref
    add constraint uk_core_entity_kinds_ref_entity_kind_name unique (entity_kind_name);

-- cc_players: rename PK to follow naming convention; add FK on status_id
alter table cc_players
    rename constraint pk_players to pk_cc_players;

alter table cc_players
    add constraint fk_cc_players_status_id foreign key (status_id)
        references core_entity_statuses_ref on update restrict on delete restrict;

-- core_actions: drop temporary DEFAULT on entity_type_id; add FK on entity_type_id and user_id
alter table core_actions
    alter column entity_type_id drop default;

alter table core_actions
    add constraint fk_core_actions_entity_type_id foreign key (entity_type_id)
        references core_entity_types_ref on update restrict on delete restrict,
    add constraint fk_core_actions_user_id foreign key (user_id)
        references cc_players on update restrict on delete restrict;

-- cc_lobbies: add PK; add FK/AK/UK constraints
alter table cc_lobbies
    add constraint pk_cc_lobbies primary key (lobby_id);

alter table cc_lobbies
    add constraint fk_cc_lobbies_owner_id foreign key (owner_id)
        references cc_players on update restrict on delete restrict,
    add constraint fk_cc_lobbies_status_id foreign key (status_id)
        references core_entity_statuses_ref on update restrict on delete restrict,
    add constraint uk_cc_lobbies_lobby_name unique (lobby_name),
    add constraint ak_cc_lobbies_lobby_code unique (lobby_code);

-- cc_lobbies_hist: fix column name typo (actual_fate -> actual_date)
alter table cc_lobbies_hist
    rename column actual_fate to actual_date;

-- Drop core_entities — all FK dependencies were removed in V2
drop table core_entities;
