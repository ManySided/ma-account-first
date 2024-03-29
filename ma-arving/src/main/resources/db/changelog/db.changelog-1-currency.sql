--liquibase formatted sql

--changeset parshakov-as:20240317-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'currency';
create table spatium.currency
(
    currency_id      bigint        not null
        constraint currency_pkey primary key,
    nazvanie         varchar(4096) not null,
    nazvanie_kratkoe varchar(4096) not null,
    znachok          varchar(4096)
);

comment on column spatium.currency.nazvanie is 'Название валюты';
comment on column spatium.currency.nazvanie_kratkoe is 'Краткое название валюты';
comment on column spatium.currency.znachok is 'Символ валюты';

--changeset parshakov-as:20240317-02 failOnError:true
create sequence if not exists spatium.currency_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.currency_id_seq
    owner to core;