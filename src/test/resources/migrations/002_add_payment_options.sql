BEGIN;

CREATE TABLE IF NOT EXISTS ebock.payment_option(
   payment_optn_id SERIAL,
   name VARCHAR(50) NOT NULL,
   PRIMARY KEY(payment_optn_id),
   UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS ebock.item_paym_option(
   item_id INTEGER,
   payment_optn_id INTEGER,
   PRIMARY KEY(item_id, payment_optn_id),
   FOREIGN KEY(item_id) REFERENCES ebock.item(item_id),
   FOREIGN KEY(payment_optn_id) REFERENCES ebock.payment_option(payment_optn_id)
);

COMMIT;