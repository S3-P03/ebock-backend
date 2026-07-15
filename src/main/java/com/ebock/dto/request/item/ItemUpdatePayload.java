package com.ebock.dto.request.item;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public class ItemUpdatePayload {
    @Size(max=60)
    public String name;

    @Size(max=350)
    public String description;

    @DecimalMin(value="0.00")
    public BigDecimal price;

    @Min(value=0)
    public Integer quantity;

    @Min(value=1)
    public Integer categoryId;

    @Min(value=1)
    public Integer wearId;

    public List<Integer> tagList;
    public List<ItemImageElement> imageList;
    public List<Integer> paymentOptionList;
    public List<Integer> deliveryOptionList;
}
