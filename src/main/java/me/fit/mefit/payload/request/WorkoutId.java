package me.fit.mefit.payload.request;

import javax.validation.constraints.NotBlank;

public class WorkoutId {

        @NotBlank
        private long id;

        public long getId() {
                return id;
        }
}
