package me.fit.mefit.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordChangeRequest {

    @NotBlank
    @Size(max = 120)
    private String password;

    public PasswordChangeRequest(@NotBlank @Size(max = 120) String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(max = 120) String password) {
        this.password = password;
    }
}
