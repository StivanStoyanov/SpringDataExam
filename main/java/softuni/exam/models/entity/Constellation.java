package softuni.exam.models.entity;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "constellations")
public class Constellation extends BaseEntity{


    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;

//    @OneToMany
//    private Set<Star> stars;

    public Constellation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public Set<Star> getStars() {
//        return stars;
//    }
//
//    public void setStars(Set<Star> stars) {
//        this.stars = stars;
//    }
}
