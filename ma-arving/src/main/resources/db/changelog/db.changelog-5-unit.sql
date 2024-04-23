--liquibase formatted sql

--changeset parshakov-as:20240422-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'unit_goods';
create table spatium.unit_goods
(
    unit_goods_id    bigint        not null
        constraint unit_pkey primary key,
    nazvanie         varchar(4096) not null,
    nazvanie_kratkoe varchar(4096) not null,
    size_base        numeric(9, 2),
    base             bigint
);

comment on column spatium.unit_goods.nazvanie is 'Название ед. измерения';
comment on column spatium.unit_goods.nazvanie_kratkoe is 'Краткое название ед. измерения';
comment on column spatium.unit_goods.size_base is 'Размер ед. измерения';
comment on column spatium.unit_goods.base is 'Базовая ед. измерения';

create sequence if not exists spatium.unit_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.unit_id_seq
    owner to core;