-- V2: Add entity fields (status_id, create_date, modify_date, close_date) directly to
--     users table and create cc_users_hist table.
--     Data for existing rows is migrated from core_entities.
--     Also adds entity_type_id to core_actions and drops FK dependencies on core_entities.

-- =====================================================================
-- users: add new columns
-- =====================================================================
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS status_id   tidcode   NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS create_date tdatetime NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS modify_date tdatetime NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS close_date  tdatetime;

-- Migrate existing values from core_entities
UPDATE users u
SET status_id   = e.entity_status_id,
    create_date = e.create_date,
    modify_date = COALESCE(e.modify_date, e.create_date),
    close_date  = e.close_date
FROM core_entities e
WHERE e.entity_id = u.user_id;

-- Remove DEFAULT after migration (value is now mandatory from application)
ALTER TABLE users
    ALTER COLUMN status_id   DROP DEFAULT,
    ALTER COLUMN create_date DROP DEFAULT,
    ALTER COLUMN modify_date DROP DEFAULT;

-- Drop FK constraint from users to core_entities
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS fk_product_users_id;

-- Enable DB sequence as default for user_id
ALTER TABLE users
    ALTER COLUMN user_id SET DEFAULT nextval('seq_action_id');

-- =====================================================================
-- cc_users_hist: create history table
-- =====================================================================
CREATE TABLE IF NOT EXISTS cc_users_hist
(
    user_id       tidbigcode not null,
    actual_date   tdatetime  not null,
    user_login    tstr100,
    first_name    tstr100,
    last_name     tstr100,
    email         tstr100,
    password_hash tstr1000,
    status_id     tidcode,
    create_date   tdatetime,
    modify_date   tdatetime,
    close_date    tdatetime
);

ALTER TABLE cc_users_hist
    owner to dev_goods_admin;

-- =====================================================================
-- core_actions: add entity_type_id, enable sequence default for action_id,
--               drop FK to core_entities
-- =====================================================================
ALTER TABLE core_actions
    ADD COLUMN IF NOT EXISTS entity_type_id tidcode NOT NULL DEFAULT 0;

-- Enable DB sequence as default for action_id
ALTER TABLE core_actions
    ALTER COLUMN action_id SET DEFAULT nextval('seq_action_id');

-- Drop FK from core_actions to core_entities
ALTER TABLE core_actions
    DROP CONSTRAINT IF EXISTS fk_actions_entity_id;
