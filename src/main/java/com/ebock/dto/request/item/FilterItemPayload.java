package com.ebock.dto.request.item;

import java.math.BigDecimal;
import java.util.ArrayList;


public class FilterItemPayload {
    BigDecimal minPrice;
    BigDecimal maxPrice;
    int maxDistance;
    Boolean favorite; //à ajouterrrr
    ArrayList<Integer> listCategoryId;
    ArrayList<Integer> listTagId; //plusieurs dans item
    ArrayList<Integer> listWearId;
    ArrayList<Integer> listDeliveryId; //plusieurs dans item
    ArrayList<Integer> listPaymentId; //plusieurs dans item

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

    public ArrayList<Integer> getListCategoryId() {
        return listCategoryId;
    }

    public ArrayList<Integer> getListTagId() {
        return listTagId;
    }

    public ArrayList<Integer> getListWearId() {
        return listWearId;
    }

    public ArrayList<Integer> getListDeliveryId() {
        return listDeliveryId;
    }

    public ArrayList<Integer> getListPaymentId() {
        return listPaymentId;
    }
}