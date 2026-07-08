package com.ebock.converter;

import com.ebock.business.Address;
import com.ebock.dto.request.user.EditAddressPayload;
import com.ebock.dto.response.user.ProfileAddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface AddressConverter {
    @Mapping(target = "addressId", ignore = true)
    Address toBusiness(EditAddressPayload payload);
    ProfileAddressResponse toProfileAddressResponse(Address address);
}

