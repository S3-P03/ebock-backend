package com.ebock.converter;

import com.ebock.business.Address;
import com.ebock.business.Category;
import com.ebock.dto.request.category.CategoryPayload;
import com.ebock.dto.request.user.AddressPayload;
import com.ebock.dto.response.category.CategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface AddressConverter {
    Address toBusiness(AddressPayload payload);
}

