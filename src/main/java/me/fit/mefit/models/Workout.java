package me.fit.mefit.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Where(clause = "DELETED = 0") //Use soft delete
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String type;


    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
    private Set<WorkoutSet> workoutSets = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    List<Program> program;

    @JsonIgnore
    @Column(name = "DELETED")
    private Integer deleted = 0;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<WorkoutSet> getExerciseSets() {
        return workoutSets;
    }

    public void setExerciseSets(Set<WorkoutSet> workoutSets) {
        this.workoutSets = workoutSets;
    }

    public List<Program> getProgram() {
        return program;
    }

    public void setProgram(List<Program> program) {
        this.program = program;
    }

    public void setDeleted() {
        this.deleted = 1;
    }
}
