--liquibase formatted sql

--changeset parshakov-as:20240816-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
alter table spatium.operations add column if not exists import_flag boolean;

comment on column spatium.operations.import_flag is 'Флаг импортированной записи';

