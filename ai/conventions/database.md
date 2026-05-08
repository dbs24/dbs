
# Database Naming Conventions

- **Naming Style:** Use `snake_case` for all database objects (tables, columns, indexes).
- **Foreign Keys:** Must follow the pattern `FK_<TABLE_NAME>_<COLUMN_NAME>`.
    - *Example:* `FK_CC_LOBBIES_OWNER_ID`
- **Primary Keys:** Use the pattern `PK_<TABLE_NAME>`.
- **Unique Constraints:** Use the pattern `UK_<TABLE_NAME>_<COLUMN_NAME>`.
- **Alternate Constraints:** Use the pattern `AK_<TABLE_NAME>_<COLUMN_NAME>`.
- **Indexes:** Use the pattern `IDX_<TABLE_NAME>_<COLUMN_NAME>`.
- **SQL Scripts:** All constraints must be explicitly named in SQL migration scripts.

Erroneous key names should be suggested for correction when creating the migration script.

# R2DBC repositories

- new database repositories should inherit interface **CoroutineCrudRepository**
- new repository functions should stand as suspend