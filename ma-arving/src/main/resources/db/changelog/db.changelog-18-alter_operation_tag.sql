--liquibase formatted sql

--changeset parshakov-as:20250109-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
alter table spatium.operation_tag add column if not exists account_id bigint not null ;

comment on column spatium.operation_tag.account_id is 'Ссылка на счёт';
