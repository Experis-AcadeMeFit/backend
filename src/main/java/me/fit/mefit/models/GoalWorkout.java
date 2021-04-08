package me.fit.mefit.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.fit.mefit.utils.ApiPaths;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Where(clause = "DELETED = 0")
public class GoalWorkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endDate;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "GOAL_ID")
    private Goal goal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Workout_ID")
    private Workout workout;


    private boolean completed = false;

    @JsonIgnore
    @Column(name = "DELETED")
    private Integer deleted = 0;

    @JsonGetter("workout")
    public String workoutGetter() {
        if(workout != null){
            return ApiPaths.WORKOUT_PATH +"/"+ workout.getId();
        }
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted() {
        this.deleted = 1;
    }
}
