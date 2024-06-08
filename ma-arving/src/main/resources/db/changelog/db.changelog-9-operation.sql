--liquibase formatted sql

--changeset parshakov-as:20240422-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_schema = 'spatium' and table_name = 'operations';
create table spatium.operations
(
    operations_id     bigint        not null
        constraint operation_pkey primary key,
    summa             numeric(9, 2) not null,
    nazvanie_opercii  varchar(4096) not null,
    komment_k_opercii varchar(4096),
    product_id        bigint
        constraint operation_category_id_fk
            references spatium.product_category,
    purchase_id       bigint
        constraint operation_purchase_id_fk
            references spatium.purchase,
    moving_in_ofu_id  bigint        not null
        constraint operation_ticket_id_fk
            references spatium.moving_in_ofu,
    is_aktivno        boolean       not null,
    is_stuff_only     boolean       not null
);

comment on column spatium.operations.summa is 'Сумма операции';
comment on column spatium.operations.nazvanie_opercii is 'Название операции';
comment on column spatium.operations.komment_k_opercii is 'Комментарий к операции';
comment on column spatium.operations.product_id is 'ID категории операции';
comment on column spatium.operations.purchase_id is 'ID покупки';
comment on column spatium.operations.moving_in_ofu_id is 'ID чека';
comment on column spatium.operations.is_aktivno is 'Флаг активности операции';
comment on column spatium.operations.is_stuff_only is 'Флаг служебная операция';

create sequence if not exists spatium.operation_id_seq
    start with 25000
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
alter table spatium.operation_id_seq
    owner to core;