package me.fit.mefit.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Where(clause = "DELETED = 0")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;

    private boolean completed(){
        if(goalWorkouts.isEmpty()){
            return true;
        }
        for (GoalWorkout goalworkout : goalWorkouts){
            if (!goalworkout.isCompleted()){
                return false;
            }
        }
        return true;
    }
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL)
    private List<GoalWorkout> goalWorkouts = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @JsonIgnore
    @Column(name = "DELETED")
    private Integer deleted = 0;

    public boolean getCompleted() {
        return completed();
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonIgnore
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    @JsonIgnore
    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted() {
        this.deleted = 1;
    }

    public List<GoalWorkout> getGoalWorkouts() {
        return goalWorkouts;
    }

    public void setGoalWorkouts(List<GoalWorkout> goalWorkouts) {
        this.goalWorkouts = goalWorkouts;
    }



}
