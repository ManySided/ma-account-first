--liquibase formatted sql

--changeset parshakov-as:20240422-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'goods';
create table spatium.goods
(
    goods_id              bigint        not null
        constraint product_pkey primary key,
    goods_nomenclature_id bigint        not null
        constraint product_nomenclature_id_fk
            references spatium.goods_nomenclature,
    marka                 varchar(4096) not null
);

comment on column spatium.goods.goods_nomenclature_id is 'ID номенклатуры товара';
comment on column spatium.goods.marka is 'Марка товара';

create sequence if not exists spatium.product_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.product_id_seq
    owner to core;