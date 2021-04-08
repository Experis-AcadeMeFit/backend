package me.fit.mefit.payload.request;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class GoalWorkoutPatch {

    private Date endDate;

    private Boolean completed;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
