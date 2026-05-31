
create table ft_projects
(
    project_id         tidbigcode default nextval('seq_action_id') not null
        constraint pk_ft_projects
            primary key,
    short_name      tstr100                                               not null
        constraint ak_ft_projects_short_name
            unique,
    owner_id      tidbigcode                                               not null
        constraint fk_ft_projects_owner_id
            references ft_users
            on update restrict on delete restrict,
    full_name          tstr200,
    status_id         tidcode                                               not null
        constraint fk_ft_projects_status_id
            references core_entity_statuses_ref
            on update restrict on delete restrict,
    create_date       tdatetime                                             not null,
    modify_date       tdatetime                                             not null,
    close_date        tdatetime
);

alter table ft_projects
    owner to dev_family_admin;

