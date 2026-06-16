package com.ebock.dto.payload.item;

import java.math.BigDecimal;
import java.util.List;

public class ItemInsertPayload {
    public String name;
    public String description;
    public BigDecimal price;
    public int quantity;
    public int categoryId;
    public int wearId;
    public List<Integer> tagList;
}
