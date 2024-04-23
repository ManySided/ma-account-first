package ru.make.account.core.arving.model.debt;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_debt_operation",
        schema = "spatium",
        sequenceName = "debt_operation_id_seq",
        allocationSize = 1)
@Table(name = "debt_moving", schema = "spatium")
public class DebtOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_debt_operation")
    @Column(name = "debt_moving_id")
    private Long id;

    @Column(name = "pervii_vznos")
    private Boolean firstOperation;

    @Column(name = "moving_in_ofu_id")
    private Long ticketId;

    @Column(name = "debt_id")
    private Long debtId;

    @Column(name = "status_udalen")
    private Boolean deleteFlag;
}
