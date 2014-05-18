package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "traders")
public class Offer {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Product product;

    @NotNull
    @Column(nullable = false)
    private Integer amount;

    @NotNull
    @Column(nullable = false)
    private Integer pricePerUnit;

    @NotNull
    @Column(nullable = false)
    private ProductQuality quality;
}
