package me.fit.mefit.payload.request;

import javax.validation.constraints.NotBlank;

public class SetRequest {

    @NotBlank
    private int exerciseRepetitions;

    @NotBlank
    private int sets;

    @NotBlank
    private Long exerciseId;

    public int getExerciseRepetitions() {
        return exerciseRepetitions;
    }

    public void setExerciseRepetitions(int exerciseRepetitions) {
        this.exerciseRepetitions = exerciseRepetitions;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

}
