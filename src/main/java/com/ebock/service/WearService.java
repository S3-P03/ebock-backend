package com.ebock.service;

import com.ebock.business.Wear;
import com.ebock.converter.WearConverter;
import com.ebock.dto.response.wear.WearPayload;
import com.ebock.dto.response.wear.WearResponse;
import com.ebock.mapper.WearMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/wear")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WearService {
    @Inject
    WearMapper wearMapper;
    @Inject
    WearConverter wearConverter;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/list/")
    @PermitAll
    public List<WearResponse> list() {
        List<Wear> wears = this.wearMapper.getAllWears();
        return wearConverter.toResponse(wears);
    }

    @POST
    @Path("/insert/")
    @Authenticated
    public WearResponse insert(WearPayload payload) {
        Wear wear = wearConverter.toBusiness(payload);
        this.wearMapper.insert(wear);
        return wearConverter.toResponse(wear);
    }

    @PUT
    @Path("/update/{id}")
    @Authenticated
    public WearResponse update(@PathParam("id") int id, WearPayload payload) {
        Wear wear = wearConverter.toBusiness(payload);
        wear.wearId = id;
        this.wearMapper.update(wear);
        return wearConverter.toResponse(wear);
    }

}