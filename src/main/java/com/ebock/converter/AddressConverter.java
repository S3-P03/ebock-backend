package com.ebock.converter;

import com.ebock.business.Address;
import com.ebock.dto.request.user.AddressPayload;
import com.ebock.dto.response.user.ProfileAddressResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface AddressConverter {
    Address toBusiness(AddressPayload payload);
    ProfileAddressResponse toProfileAddressResponse(Address address);
}

