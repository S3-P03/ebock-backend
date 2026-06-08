package com.ebock.converter;

import com.ebock.business.Wear;
import com.ebock.dto.response.wear.WearPayload;
import com.ebock.dto.response.wear.WearResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface WearConverter {
    WearResponse toResponse(Wear wear);
    List<WearResponse> toResponse(List<Wear> wears);
    Wear toBusiness(WearPayload payload);
}

