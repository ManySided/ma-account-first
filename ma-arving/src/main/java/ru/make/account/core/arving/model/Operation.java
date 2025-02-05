package ru.make.account.core.arving.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@SequenceGenerator(
        name = "int_id_generator_operation",
        schema = "spatium",
        sequenceName = "operation_id_seq",
        allocationSize = 1)
@Table(name = "operations", schema = "spatium")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "int_id_generator_operation")
    @Column(name = "operations_id")
    private Long id;

    @Column(name = "summa")
    private BigDecimal sum;

    @Column(name = "nazvanie_opercii", nullable = false)
    private String name;

    @Column(name = "komment_k_opercii")
    private String comment;

    @Column(name = "product_id")
    private Long categoryId;

    @Column(name = "purchase_id")
    private Long purchaseId;

    @Column(name = "moving_in_ofu_id")
    private Long ticketId;

    @Column(name = "is_aktivno")
    private Boolean isActive;

    @Column(name = "is_stuff_only")
    private Boolean stuffFlag;

    @Column(name = "import_flag")
    private Boolean importFlag;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "operation_to_tag",
            schema = "spatium",
            joinColumns = {@JoinColumn(name = "operation_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private Set<OperationTag> tags = new HashSet<>();
}
