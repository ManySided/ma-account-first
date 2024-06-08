package ru.make.account.core.arving.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_money_transfer",
        schema = "spatium",
        sequenceName = "money_transfer_id_seq",
        allocationSize = 1)
@Table(name = "remittance", schema = "spatium")
public class MoneyTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_money_transfer")
    @Column(name = "remittance_id")
    private Long id;

    @Column(name = "is_aktivno")
    private Boolean activeFlag;

    @Column(name = "data_perevoda")
    private LocalDate transferDate;

    @Column(name = "user_portal_id")
    private UUID userId;

    @Column(name = "summa_perevoda")
    private BigDecimal transferSum;

    @Column(name = "moving_snatia")
    private Long inTicketId;

    @Column(name = "moving_zachislenia")
    private Long outTicketId;

    @Column(name = "komment")
    private String comment;
}
