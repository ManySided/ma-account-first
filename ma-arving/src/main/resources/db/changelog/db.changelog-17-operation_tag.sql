--liquibase formatted sql

--changeset parshakov-as:20250109-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'operation_tag';
create table spatium.operation_tag
(
    operation_tag_id bigint        not null
        constraint operation_tag_pkey primary key,
    name             varchar(4096) not null,
    color            varchar(4096),
    sign             varchar(4096)
);

comment on column spatium.operation_tag.name is 'Название тега';

--changeset parshakov-as:20250109-02 failOnError:true
create sequence if not exists spatium.operation_tag_id_seq
    start with 1
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.operation_tag_id_seq
    owner to core;

--changeset parshakov-as:20250109-03 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'operation_to_tag';
create table spatium.operation_to_tag
(
    operation_id bigint not null references spatium.operations (operations_id),
    tag_id       bigint not null references spatium.operation_tag (operation_tag_id),
    constraint operation_to_tag_pkey primary key (operation_id, tag_id)
)
