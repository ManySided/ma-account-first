--liquibase formatted sql

--changeset parshakov-as:20240412-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'product_category';
create table spatium.product_category
(
    product_id             bigint        not null
        constraint product_category_pkey primary key,
    nazvanie_kategorii     varchar(4096) not null,
    parent                 bigint,
    ofu_id                 bigint        not null
        constraint category_ofu_id_fk
            references spatium.ofu,
    is_kategoria_slugebnaa boolean       not null
);

comment on column spatium.product_category.nazvanie_kategorii is 'Название категории';
comment on column spatium.product_category.parent is 'Родительская категория';
comment on column spatium.product_category.ofu_id is 'Идентификатор счёта';
comment on column spatium.product_category.is_kategoria_slugebnaa is 'Флаг служебной категории';

create sequence if not exists spatium.category_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.category_id_seq
    owner to core;