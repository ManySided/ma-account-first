package ru.make.account.core.arving.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_account",
        schema = "spatium",
        sequenceName = "account_id_seq",
        allocationSize = 1)
@Table(name = "ofu", schema = "spatium")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_account")
    @Column(name = "ofu_id")
    private Long id;

    @Column(name = "nazvanie")
    private String name;

    @Column(name = "kommentarii")
    private String comment;

    @Column(name = "init_state")
    private BigDecimal startSum;

    @Column(name = "current_state", nullable = false)
    private BigDecimal currentSum;

    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "is_aktivno")
    private Boolean actual;

    @Column(name = "user_portal_id")
    private UUID accountCreator;

    @Column(name = "created_date")
    private LocalDateTime created;
}
