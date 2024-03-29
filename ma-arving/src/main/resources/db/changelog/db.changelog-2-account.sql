--liquibase formatted sql

--changeset parshakov-as:20240320-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'ofu';
create table spatium.ofu
(
    ofu_id         bigint                      not null
        constraint ofu_pkey primary key,
    nazvanie       varchar(4096)               not null,
    kommentarii    varchar(4096),
    init_state     numeric(9, 2)               not null,
    current_state  numeric(9, 2)               not null,
    currency_id    bigint                      not null
        constraint account_currency_id_fk
            references spatium.currency,
    user_portal_id uuid                        not null,
    created_date   timestamp without time zone not null,
    is_aktivno     boolean                     not null
);

comment on column spatium.ofu.nazvanie is 'Название счёта';
comment on column spatium.ofu.kommentarii is 'Комментарий к счёту';
comment on column spatium.ofu.init_state is 'Начальная сумма';
comment on column spatium.ofu.current_state is 'Текущая сумма';
comment on column spatium.ofu.currency_id is 'ID валюты счёта';
comment on column spatium.ofu.is_aktivno is 'Признак активности счёта';

create sequence if not exists spatium.account_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.account_id_seq
    owner to core;