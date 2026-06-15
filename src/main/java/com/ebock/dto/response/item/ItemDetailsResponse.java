package com.ebock.dto.response.item;

import java.math.BigDecimal;
import java.util.List;

public class ItemDetailsResponse {
    public int itemId;
    public String name;
    public String description;
    public BigDecimal price;
    public String addedAt;
    public int quantity;
    public String category;
    public String wear;
    public List<String> paymentOptions;
    public List<String> deliveryOptions;
    public String sellerCip;
    public List<Integer> tags;
}
