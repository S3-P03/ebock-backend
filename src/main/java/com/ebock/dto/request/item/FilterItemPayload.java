package com.ebock.dto.request.item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;


public class FilterItemPayload {
    @DecimalMin(value = "0.0", inclusive = true)
    public BigDecimal minPrice;
    @DecimalMin(value = "0.0", inclusive = true)
    public BigDecimal maxPrice;
    @Min(0)
    int maxDistance;
    public Boolean favorite;
    public List<Integer> listCategoryId;
    public List<Integer> listTagId;
    public List<Integer> listWearId;
    public List<Integer> listDeliveryId;
    public List<Integer> listPaymentId;

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public List<Integer> getListCategoryId() {
        return listCategoryId;
    }

    public List<Integer> getListTagId() {
        return listTagId;
    }

    public List<Integer> getListWearId() {
        return listWearId;
    }

    public List<Integer> getListDeliveryId() {
        return listDeliveryId;
    }

    public List<Integer> getListPaymentId() {
        return listPaymentId;
    }
}