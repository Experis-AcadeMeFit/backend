package me.fit.mefit.models;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Where(clause = "DELETED = 0")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date endDate;
    private Date startDate;
   // private List<GoalWorkout> workouts;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;


    //achieved => compute from workouts > sets > completed
}
