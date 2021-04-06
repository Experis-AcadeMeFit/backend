package me.fit.mefit.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class WorkoutRequest {

    @NotBlank
    @Size(max = 40)
    private String name;

    @NotBlank
    @Size(max = 40)
    private String type;

    private List<SetRequest> exerciseSets;

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

    public List<SetRequest> getExerciseSets() {
        return exerciseSets;
    }

    public void setExerciseSets(List<SetRequest> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }

}
