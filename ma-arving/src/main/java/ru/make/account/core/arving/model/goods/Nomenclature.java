package ru.make.account.core.arving.model.goods;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_nomenclature",
        schema = "spatium",
        sequenceName = "nomenclature_id_seq",
        allocationSize = 1)
@Table(name = "goods_nomenclature", schema = "spatium")
public class Nomenclature {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_nomenclature")
    @Column(name = "goods_nomenclature_id")
    private Long id;

    @Column(name = "nazvanie")
    private String name;
}
