package me.fit.mefit.models;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Where(clause = "DELETED = 0")
public class GoalWorkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date endDate;
   // private Goal goalId;
    //private Workout workoutId;
    private boolean completed;

    //
}
