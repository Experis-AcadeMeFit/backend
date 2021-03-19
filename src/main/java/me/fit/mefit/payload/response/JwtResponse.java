package me.fit.mefit.payload.response;

import java.util.List;

public class JwtResponse {
    private String accessToken;
    private String type = "Bearer";
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, String email, List<String> roles) {
        this.accessToken = accessToken;
        this.email = email;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
