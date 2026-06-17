package com.ebock.dto.request.item;

import java.math.BigDecimal;
import java.util.ArrayList;

public class FilterItemPayload {
    BigDecimal minPrice;
    BigDecimal maxPrice;
    int maxDistance;
    Boolean favorite;
    ArrayList<Integer> listCategoryId;
    ArrayList<Integer> listTagId; //plusieurs dans item
    ArrayList<Integer> listWearId;
    ArrayList<Integer> listDeliveryId; //plusieurs dans item
    ArrayList<Integer> listPaymentId; //plusieurs dans item
}