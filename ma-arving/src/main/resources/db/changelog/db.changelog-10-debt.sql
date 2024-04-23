--liquibase formatted sql

--changeset parshakov-as:20240423-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'debt';
create table spatium.debt
(
    debt_id          bigint        not null
        constraint debt_pkey primary key,
    is_aktivno       boolean       not null,
    data_sozdania    DATE          not null DEFAULT CURRENT_DATE,
    data_pogashenia  DATE,
    nazvanie         varchar(4096) not null,
    komment          varchar(4096),
    summa_dolga      numeric(9, 2) not null,
    curr_summa_dolga numeric(9, 2) not null,
    ofu_id           bigint        not null
        constraint debt_ofu_id_fk
            references spatium.ofu,
    napr_dolga       boolean       not null,
    status_udalen    boolean       not null
);

comment on column spatium.debt.is_aktivno is 'Статус активности долга';
comment on column spatium.debt.data_sozdania is 'Дата создания';
comment on column spatium.debt.data_pogashenia is 'Дата погашения';
comment on column spatium.debt.nazvanie is 'Название долга';
comment on column spatium.debt.komment is 'Описание долга';
comment on column spatium.debt.summa_dolga is 'Сумма долга';
comment on column spatium.debt.curr_summa_dolga is 'Текущая сумма долга';
comment on column spatium.debt.ofu_id is 'ID счёта';
comment on column spatium.debt.napr_dolga is 'Флаг должника';
comment on column spatium.debt.status_udalen is 'Признак удаления долга';

create sequence if not exists spatium.debt_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.debt_id_seq
    owner to core;

--changeset parshakov-as:20240423-02 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'debt_moving';
create table spatium.debt_moving
(
    debt_moving_id   bigint  not null
        constraint debt_operation_pkey primary key,
    pervii_vznos     boolean not null,
    moving_in_ofu_id bigint  not null
        constraint debt_operation_ticket_id_fk
            references spatium.moving_in_ofu,
    debt_id          bigint  not null
        constraint debt_operation_debt_id_fk
            references spatium.debt,
    status_udalen    boolean not null
);

comment on column spatium.debt_moving.pervii_vznos is 'Признак первой операции';
comment on column spatium.debt_moving.moving_in_ofu_id is 'ID чека';
comment on column spatium.debt_moving.debt_id is 'ID долга';
comment on column spatium.debt_moving.status_udalen is 'Признак удалёния операции';

create sequence if not exists spatium.debt_operation_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.debt_operation_id_seq
    owner to core;