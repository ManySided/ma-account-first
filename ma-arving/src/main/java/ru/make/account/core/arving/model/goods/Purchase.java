package ru.make.account.core.arving.model.goods;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_purchase",
        schema = "spatium",
        sequenceName = "purchase_id_seq",
        allocationSize = 1)
@Table(name = "purchase", schema = "spatium")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_purchase")
    @Column(name = "purchase_id")
    private Long id;

    @Column(name = "goods_id")
    private Long productId;

    @Column(name = "kolichestvo")
    private BigDecimal quantity;

    @Column(name = "stoimost_ed")
    private BigDecimal unitCost;

    @Column(name = "goods_unit")
    private Long unitId;

    @Column(name = "skidka")
    private BigDecimal discount;
}
