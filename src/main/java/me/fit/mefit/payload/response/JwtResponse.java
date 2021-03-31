package me.fit.mefit.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String scope;
    private String sessionState;
    private int expiresIn;
    private int refreshExpiresIn;
    private int notBeforePolicy;

    public JwtResponse() {
    }

    public JwtResponse(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }
    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    @JsonProperty("refresh_token")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("session_state")
    public String getSessionState() {
        return sessionState;
    }

    @JsonProperty("session_state")
    public void setSessionState(String sessionState) {
        this.sessionState = sessionState;
    }

    @JsonProperty("expires_in")
    public int getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("expires_in")
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @JsonProperty("refresh_expires_in")
    public int getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    @JsonProperty("refresh_expires_in")
    public void setRefreshExpiresIn(int refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    @JsonProperty("not_before_policy")
    public int getNotBeforePolicy() {
        return notBeforePolicy;
    }

    @JsonProperty("not_before_policy")
    public void setNotBeforePolicy(int notBeforePolicy) {
        this.notBeforePolicy = notBeforePolicy;
    }
}
