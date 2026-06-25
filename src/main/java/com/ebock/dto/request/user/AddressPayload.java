package com.ebock.dto.request.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressPayload {
    @Min(1)
    public Integer civicNumber;

    @Min(1)
    public Integer apptNumber;

    @Size(max=60)
    public String street;

    @Size(max=60)
    public String city;

    @Pattern(regexp = "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$")
    public String postalCode;

    @Size(min=2, max=2)
    public String provinceCode;

    @Size(max=30)
    public String country;
}
