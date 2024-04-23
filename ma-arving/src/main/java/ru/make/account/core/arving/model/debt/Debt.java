package ru.make.account.core.arving.model.debt;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_debt",
        schema = "spatium",
        sequenceName = "debt_id_seq",
        allocationSize = 1)
@Table(name = "debt", schema = "spatium")
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_debt")
    @Column(name = "debt_id")
    private Long id;

    @Column(name = "is_aktivno")
    private Boolean isActive;

    @Column(name = "data_sozdania")
    private LocalDate createDate;

    @Column(name = "data_pogashenia")
    private LocalDate closeDate;

    @Column(name = "nazvanie")
    private String name;

    @Column(name = "komment")
    private String description;

    @Column(name = "summa_dolga")
    private BigDecimal sumDebt;

    @Column(name = "curr_summa_dolga")
    private BigDecimal sumDebtCurrent;

    @Column(name = "ofu_id")
    private Long accountId;

    @Column(name = "napr_dolga")
    private Boolean debtorFlag;

    @Column(name = "status_udalen")
    private Boolean deleteFlag;
}
