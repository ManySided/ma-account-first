--liquibase formatted sql

--changeset parshakov-as:20240828-01 failOnError:true
--preconditions onFail:MARK_RAN onError:HALT
ALTER Table spatium.ofu
    ALTER init_state TYPE numeric(15, 2);
ALTER Table spatium.ofu
    ALTER current_state TYPE numeric(15, 2);