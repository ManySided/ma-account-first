package ru.make.account.core.arving.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_category",
        schema = "spatium",
        sequenceName = "category_id_seq",
        allocationSize = 1)
@Table(name = "product_category", schema = "spatium")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_category")
    @Column(name = "product_id")
    private Long id;
    @Column(name = "nazvanie_kategorii")
    private String name;
    @Column(name = "parent")
    private Long parent;
    @Column(name = "ofu_id")
    private Long accountId;
    @Column(name = "is_kategoria_slugebnaa")
    private Boolean stuffFlag;
    @Column(name = "flag_activity")
    private Boolean flagActivity;
}
