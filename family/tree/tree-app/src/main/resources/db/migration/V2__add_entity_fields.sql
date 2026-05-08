-- V2: Add entity fields (status_id, create_date, modify_date, close_date) directly to
--     cc_players, cc_players_hist, cc_lobbies, cc_lobbies_hist tables.
--     Data for existing rows is migrated from core_entities.
--     Also adds entity_type_id to core_actions and drops FK dependencies on core_entities.

-- =====================================================================
-- cc_players: add new columns
-- =====================================================================
ALTER TABLE cc_players
    ADD COLUMN IF NOT EXISTS status_id   tidcode   NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS create_date tdatetime NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS modify_date tdatetime NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS close_date  tdatetime;

-- Migrate existing values from core_entities
UPDATE cc_players p
SET status_id   = e.entity_status_id,
    create_date = e.create_date,
    modify_date = COALESCE(e.modify_date, e.create_date),
    close_date  = e.close_date
FROM core_entities e
WHERE e.entity_id = p.player_id;

-- Remove DEFAULT after migration (value is now mandatory from application)
ALTER TABLE cc_players
    ALTER COLUMN status_id   DROP DEFAULT,
    ALTER COLUMN create_date DROP DEFAULT,
    ALTER COLUMN modify_date DROP DEFAULT;

-- Drop FK constraint from cc_players to core_entities
ALTER TABLE cc_players
    DROP CONSTRAINT IF EXISTS fk_players_players_player_id;

-- Enable DB sequence as default for player_id (generated via nextval('seq_action_id'))
ALTER TABLE cc_players
    ALTER COLUMN player_id SET DEFAULT nextval('seq_action_id');

-- =====================================================================
-- cc_players_hist: add new columns (nullable, copied from source player)
-- =====================================================================
ALTER TABLE cc_players_hist
    ADD COLUMN IF NOT EXISTS status_id   tidcode,
    ADD COLUMN IF NOT EXISTS create_date tdatetime,
    ADD COLUMN IF NOT EXISTS modify_date tdatetime,
    ADD COLUMN IF NOT EXISTS close_date  tdatetime;

-- =====================================================================
-- cc_lobbies: add new columns
-- =====================================================================
ALTER TABLE cc_lobbies
    ADD COLUMN IF NOT EXISTS status_id   tidcode   NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS create_date tdatetime NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS modify_date tdatetime NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS close_date  tdatetime;

-- Migrate existing values from core_entities
UPDATE cc_lobbies l
SET status_id   = e.entity_status_id,
    create_date = e.create_date,
    modify_date = COALESCE(e.modify_date, e.create_date),
    close_date  = e.close_date
FROM core_entities e
WHERE e.entity_id = l.lobby_id;

ALTER TABLE cc_lobbies
    ALTER COLUMN status_id   DROP DEFAULT,
    ALTER COLUMN create_date DROP DEFAULT,
    ALTER COLUMN modify_date DROP DEFAULT;

-- Drop FK constraint from cc_lobbies to core_entities
ALTER TABLE cc_lobbies
    DROP CONSTRAINT IF EXISTS fk_cc_lobbies_lobby_id;

-- Enable DB sequence as default for lobby_id
ALTER TABLE cc_lobbies
    ALTER COLUMN lobby_id SET DEFAULT nextval('seq_action_id');

-- =====================================================================
-- cc_lobbies_hist: add new columns
-- =====================================================================
ALTER TABLE cc_lobbies_hist
    ADD COLUMN IF NOT EXISTS status_id   tidcode,
    ADD COLUMN IF NOT EXISTS create_date tdatetime,
    ADD COLUMN IF NOT EXISTS modify_date tdatetime,
    ADD COLUMN IF NOT EXISTS close_date  tdatetime;

-- =====================================================================
-- core_actions: add entity_type_id, enable sequence default for action_id,
--               drop FK to core_entities (core_entities table will be removed)
-- =====================================================================
ALTER TABLE core_actions
    ADD COLUMN IF NOT EXISTS entity_type_id tidcode NOT NULL DEFAULT 0;

-- Enable DB sequence as default for action_id
ALTER TABLE core_actions
    ALTER COLUMN action_id SET DEFAULT nextval('seq_action_id');

-- Drop FK from core_actions to core_entities
ALTER TABLE core_actions
    DROP CONSTRAINT IF EXISTS fk_actions_entity_id;
