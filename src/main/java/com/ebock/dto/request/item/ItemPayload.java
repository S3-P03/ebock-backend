package com.ebock.dto.request.item;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public class ItemPayload {
    @NotBlank
    @Size(max=60)
    public String name;
    @Size(max=350)
    public String description;
    @NotNull
    @DecimalMin(value="0.01")
    public BigDecimal price;
    @Min(value=1)
    public int quantity;
    @Min(value=1)
    public int categoryId;
    @Min(value=1)
    public int wearId;
    public List<Integer> tagList;
    public List<ItemImageElement> imageList;
    public List<Integer> paymentOptionList;
    public List<Integer> deliveryOptionList;
}
