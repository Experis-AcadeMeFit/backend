package me.fit.mefit.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.fit.mefit.utils.ApiPaths;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Where(clause = "DELETED = 0") //Use soft delete
@Table(name = "workout_set")
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int exerciseRepetitions;
    private int sets;

    @ManyToOne(optional = false)
    @JoinColumn(name = "EXERCISE_ID")
    private Exercise exercise;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "WORKOUT_ID")
    private Workout workout;

    @JsonIgnore
    @Column(name = "DELETED")
    private Integer deleted = 0;


    //JsonGetter returns exercise as a link
    @JsonGetter("exercise")
    public String exerciseGetter() {
        if(exercise != null){
            return ApiPaths.EXERCISE_PATH +"/"+ exercise.getId();
        }
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getExerciseRepetitions() {
        return exerciseRepetitions;
    }

    public void setExerciseRepetitions(int exerciseRepetitions) {
        this.exerciseRepetitions = exerciseRepetitions;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }


    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setDeleted() {
        this.deleted = 1;
    }


}
