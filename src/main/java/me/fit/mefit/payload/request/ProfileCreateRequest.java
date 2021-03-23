package me.fit.mefit.payload.request;

import me.fit.mefit.models.Address;


import javax.validation.constraints.NotNull;

public class ProfileCreateRequest {
    private long userId;
    @NotNull
    private Address address;
    private int weight;
    private int height;

    public ProfileCreateRequest() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
