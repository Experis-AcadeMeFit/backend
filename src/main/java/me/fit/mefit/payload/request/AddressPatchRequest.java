package me.fit.mefit.payload.request;

import javax.validation.constraints.Size;

public class AddressPatchRequest {

    @Size(max = 100, min = 1)
    private String addressLine1;

    @Size(max = 100, min = 1)
    private String addressLine2;

    @Size(max = 100, min = 1)
    private String addressLine3;

    @Size(max = 20, min = 1)
    private String postalCode;

    @Size(max = 30, min = 1)
    private String city;

    @Size(max = 40, min = 1)
    private String Country;

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return Country;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        Country = country;
    }
}
