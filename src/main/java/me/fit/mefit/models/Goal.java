package me.fit.mefit.models;

import java.util.Date;
import java.util.List;

public class Goal {
    private long id;
    private Date endDate;
    private Date startDate;
    private Program program;
    private List<GoalWorkout> workouts;


    //achieved => compute from workouts > sets > completed
}
