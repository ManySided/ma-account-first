--liquibase formatted sql

--changeset parshakov-as:20240914-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
alter table spatium.product_category add column if not exists relevant boolean;

comment on column spatium.product_category.relevant is 'Флаг релевантности категории (используется часто)';
