package ru.make.account.core.arving.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_ticket",
        schema = "spatium",
        sequenceName = "ticket_id_seq",
        allocationSize = 1)
@Table(name = "moving_in_ofu", schema = "spatium")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_ticket")
    @Column(name = "moving_in_ofu_id")
    private Long id;

    @Column(name = "napravlenie_dvigenia")
    @Enumerated(EnumType.ORDINAL)
    private TicketDirectionEnum ticketDirection;

    @Column(name = "data_dvigenia")
    private LocalDate date;

    @Column(name = "ofu_id")
    private Long accountId;

    @Column(name = "user_portal_id")
    private UUID user;

    @Column(name = "skidka_round")
    private BigDecimal discount;
}
