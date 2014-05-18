package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "traderCategories")
public class TraderCategory {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @OneToMany
    @JoinColumn(nullable = true)
    private Set<AssortmentNature> assortments = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AssortmentNature> getAssortments() {
        return assortments;
    }

    public void setAssortments(Set<AssortmentNature> assortments) {
        this.assortments = assortments;
    }
}