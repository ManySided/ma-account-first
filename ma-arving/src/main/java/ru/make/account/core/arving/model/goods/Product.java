package ru.make.account.core.arving.model.goods;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_product",
        schema = "spatium",
        sequenceName = "product_id_seq",
        allocationSize = 1)
@Table(name = "goods", schema = "spatium")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_product")
    @Column(name = "goods_id")
    private Long id;

    @Column(name = "goods_nomenclature_id")
    private Long nomenclatureId;

    @Column(name = "marka")
    private String mark;
}
