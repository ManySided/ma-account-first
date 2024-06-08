--liquibase formatted sql

--changeset parshakov-as:20240423-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'remittance';
create table spatium.remittance
(
    remittance_id      bigint        not null
        constraint money_transfer_pkey primary key,
    is_aktivno         boolean       not null,
    data_perevoda      DATE          not null DEFAULT CURRENT_DATE,
    user_portal_id     uuid          not null,
    summa_perevoda     numeric(9, 2) not null,
    komment            text,
    moving_snatia      bigint        not null
        constraint transfer_ticket_in_id_fk
            references spatium.moving_in_ofu,
    moving_zachislenia bigint        not null
        constraint transfer_ticket_out_id_fk
            references spatium.moving_in_ofu
);

comment on column spatium.remittance.is_aktivno is 'Статус активности перевода';
comment on column spatium.remittance.data_perevoda is 'Дата перевода';
comment on column spatium.remittance.user_portal_id is 'ID пользователя';
comment on column spatium.remittance.summa_perevoda is 'Сумма перевода';
comment on column spatium.remittance.moving_snatia is 'ID чека снятия';
comment on column spatium.remittance.moving_zachislenia is 'ID чека зачисления';

create sequence if not exists spatium.money_transfer_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.money_transfer_id_seq
    owner to core;