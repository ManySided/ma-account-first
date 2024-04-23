package ru.make.account.core.arving.model.goods;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_unit",
        schema = "spatium",
        sequenceName = "unit_id_seq",
        allocationSize = 1)
@Table(name = "unit_goods", schema = "spatium")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_unit")
    @Column(name = "unit_goods_id")
    private Long id;

    @Column(name = "nazvanie")
    private String name;

    @Column(name = "nazvanie_kratkoe")
    private String shortName;

    @Column(name = "size_base")
    private BigDecimal size;

    @Column(name = "base")
    private Long baseUnitId;
}
