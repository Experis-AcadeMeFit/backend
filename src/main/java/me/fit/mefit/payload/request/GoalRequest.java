package me.fit.mefit.payload.request;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class GoalRequest {
    @NotBlank
    private Date endDate;
    @NotBlank
    private Date startDate;

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




}
