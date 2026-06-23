package com.ebock.dto.request.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserEditPayload {
    @Size(max=100)
    public String newFirstName;
    @Size(max=100)
    public String newLastName;
    @Min(1)
    public Integer newcivicNumber;
    @Min(1)
    public Integer newapptNumber;
    @Size(max=60)
    public String newStreet;
    @Pattern(
            regexp = "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$"
    )
    public String newPostalCode;

    @Size(max=30)
    public String newCountry;
    @Size(min=2, max=2)
    public String newProvinceCode;
}
