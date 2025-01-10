package ru.make.account.core.arving.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_operation_tag",
        schema = "spatium",
        sequenceName = "operation_tag_id_seq",
        allocationSize = 1)
@Table(name = "operation_tag", schema = "spatium")
public class OperationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_operation_tag")
    @Column(name = "operation_tag_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "sign")
    private String sign;

    @Column(name = "account_id")
    private Long accountId;
}
