package ru.make.account.core.arving.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_currency",
        schema = "spatium",
        sequenceName = "currency_id_seq",
        allocationSize = 1)
@Table(name = "currency",schema = "spatium")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_currency")
    @Column(name = "currency_id")
    private Long id;

    @Column(name = "nazvanie")
    private String name;

    @Column(name = "nazvanie_kratkoe")
    private String shortName;

    @Column(name = "znachok")
    private String symbol;
}
