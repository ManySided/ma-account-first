--liquibase formatted sql

--changeset parshakov-as:20240915-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
update spatium.product_category set relevant=true;
