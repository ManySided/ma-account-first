--liquibase formatted sql

--changeset parshakov-as:20240422-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'purchase';
create table spatium.purchase
(
    purchase_id bigint        not null
        constraint purchase_pkey primary key,
    goods_id    bigint        not null
        constraint purchase_product_id_fk
            references spatium.goods,
    kolichestvo numeric(9, 2) not null,
    stoimost_ed numeric(9, 2) not null,
    goods_unit  bigint        not null
        constraint purchase_unit_id_fk
            references spatium.unit_goods,
    skidka      numeric(9, 2)
);

comment on column spatium.purchase.goods_id is 'ID товара';
comment on column spatium.purchase.kolichestvo is 'Количество';
comment on column spatium.purchase.stoimost_ed is 'Стоимость единицы товара';
comment on column spatium.purchase.goods_unit is 'Единицы измерения товара';
comment on column spatium.purchase.skidka is 'Скидка';

create sequence if not exists spatium.purchase_id_seq
    start with 100
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.purchase_id_seq
    owner to core;