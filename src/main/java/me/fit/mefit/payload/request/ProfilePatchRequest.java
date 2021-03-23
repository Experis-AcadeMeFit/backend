package me.fit.mefit.payload.request;

public class ProfilePatchRequest {
    private AddressPatchRequest address;
    private Integer weight;
    private Integer height;

    public AddressPatchRequest getAddress() {
        return address;
    }

    public void setAddress(AddressPatchRequest address) {
        this.address = address;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
