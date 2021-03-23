package me.fit.mefit.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import me.fit.mefit.utils.ApiPaths;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "profile")
public class Profile {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne(mappedBy = "profile")
    private User user;

    // private Goal goal;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @NotNull
    private Address address;

    //Keep?
    // private Program program;
    // private Workout workout;
    // private Set set;

    private int weight;
    private int height;

    // private String medicalCondition;
    // private String disabilities;

    @JsonGetter("user")
    public String jsonUser() {
        return ApiPaths.USER_PATH + "/" + user.getId();
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
