package com.ebock.business;

import java.math.BigDecimal;

public class Item {
    public int itemId;
    public String name;
    public String description;
    public BigDecimal price;
    public String addedAt;
    public String updatedAt;
    public Boolean sold;
    public int quantity;
    public Boolean archived;
    public int categoryId;
    public int wearId;
    public String sellerCip;
}
/*
   item_id SERIAL,
   name VARCHAR(60)  NOT NULL,
   description VARCHAR(350)  NOT NULL,
   price MONEY NOT NULL,
   added_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP,
   sold BOOLEAN NOT NULL,
   quantity SMALLINT NOT NULL,
   archived BOOLEAN NOT NULL,
   category_id INTEGER NOT NULL,
   wear_id INTEGER NOT NULL,
   seller_cip VARCHAR(8)  NOT NULL,
*/

