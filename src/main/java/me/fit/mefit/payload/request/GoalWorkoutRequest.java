package me.fit.mefit.payload.request;


import javax.validation.constraints.NotBlank;
import java.util.Date;

public class GoalWorkoutRequest {
    private Date endDate;

    @NotBlank
    private Long workoutId;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }
}
