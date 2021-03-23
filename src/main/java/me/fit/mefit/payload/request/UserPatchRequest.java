package me.fit.mefit.payload.request;

import me.fit.mefit.models.RoleEnum;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;


public class UserPatchRequest {

    @Size(max = 30, min = 1)
    private String firstName;

    @Size(max = 30, min = 1)
    private String lastName;

    @Email
    @Size(max = 50, min = 1)
    private String email;

    private List<RoleEnum> roles;

    public UserPatchRequest() {
    }

    public UserPatchRequest(@Size(max = 30, min = 1) String firstName, @Size(max = 30, min = 1) String lastName, @Email @Size(max = 50, min = 1) String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEnum> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserPatchRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
