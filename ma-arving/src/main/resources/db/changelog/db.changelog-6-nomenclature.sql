--liquibase formatted sql

--changeset parshakov-as:20240422-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'goods_nomenclature';
create table spatium.goods_nomenclature
(
    goods_nomenclature_id    bigint        not null
        constraint nomenclature_pkey primary key,
    nazvanie         varchar(4096) not null
);

comment on column spatium.goods_nomenclature.nazvanie is 'Название номенклатуры';

create sequence if not exists spatium.nomenclature_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.nomenclature_id_seq
    owner to core;