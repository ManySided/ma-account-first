--liquibase formatted sql

--changeset parshakov-as:20240419-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'moving_in_ofu';
create table spatium.moving_in_ofu
(
    moving_in_ofu_id     bigint not null
        constraint ticket_pkey primary key,
    napravlenie_dvigenia bigint not null,
    data_dvigenia        DATE   not null DEFAULT CURRENT_DATE,
    ofu_id               bigint not null
        constraint category_ofu_id_fk
            references spatium.ofu,
    user_portal_id       uuid   not null,
    skidka_round         numeric(9, 2)
);

comment on column spatium.moving_in_ofu.napravlenie_dvigenia is 'Тип чека';
comment on column spatium.moving_in_ofu.data_dvigenia is 'Дата чека';
comment on column spatium.moving_in_ofu.ofu_id is 'ID счёта';
comment on column spatium.moving_in_ofu.user_portal_id is 'Пользователь создавший счёт';
comment on column spatium.moving_in_ofu.skidka_round is 'Скидка';

create sequence if not exists spatium.ticket_id_seq
    start with 10000
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.ticket_id_seq
    owner to core;