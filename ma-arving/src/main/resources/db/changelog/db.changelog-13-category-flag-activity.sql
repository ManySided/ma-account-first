--liquibase formatted sql

--changeset parshakov-as:20240820-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
alter table spatium.product_category
    add column if not exists flag_activity boolean not null default true;

comment on column spatium.product_category.flag_activity is 'Флаг активной категории';

