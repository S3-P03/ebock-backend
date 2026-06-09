package com.ebock.dto.response.item;

import java.math.BigDecimal;
import java.util.List;

public class ItemResponse {
    public int itemId;
    public String name;
    public BigDecimal price;
    public String addedAt;
    public int quantity;
    public int categoryId;
    public int wearId;
    public String firstName;
    public String lastName;
    public List<Integer> tags;
}
