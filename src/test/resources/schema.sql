set search_path = ebock;

CREATE TABLE user_(
   cip VARCHAR(8) ,
   first_name VARCHAR(50)  NOT NULL,
   last_name VARCHAR(50)  NOT NULL,
   email VARCHAR(90)  NOT NULL,
   is_admin BOOLEAN NOT NULL,
   profile_picture_url VARCHAR(50) ,
   enabled BOOLEAN NOT NULL,
   created_at TIMESTAMP NOT NULL,
   updated_at VARCHAR(50) ,
   PRIMARY KEY(cip),
   UNIQUE(email)
);

CREATE TABLE review(
   reviewer_cip VARCHAR(8) ,
   reviewed_cip VARCHAR(8) ,
   timestamp_ TIMESTAMP,
   content VARCHAR(360)  NOT NULL,
   rating SMALLINT NOT NULL,
   updated_at TIMESTAMP,
   PRIMARY KEY(reviewer_cip, reviewed_cip, timestamp_),
   FOREIGN KEY(reviewer_cip) REFERENCES user_(cip),
   FOREIGN KEY(reviewed_cip) REFERENCES user_(cip)
);

CREATE TABLE delivery_option(
   delivery_optn_id SERIAL,
   name VARCHAR(50)  NOT NULL,
   PRIMARY KEY(delivery_optn_id),
   UNIQUE(name)
);

CREATE TABLE category(
   category_id SERIAL,
   name VARCHAR(50)  NOT NULL,
   parent_category INTEGER,
   PRIMARY KEY(category_id),
   UNIQUE(name),
   FOREIGN KEY(parent_category) REFERENCES category(category_id)
);

CREATE TABLE tag(
   tag_id SERIAL,
   name VARCHAR(50)  NOT NULL,
   PRIMARY KEY(tag_id),
   UNIQUE(name)
);

CREATE TABLE log_category(
   log_category_name VARCHAR(50) ,
   PRIMARY KEY(log_category_name)
);

CREATE TABLE wear(
   wear_id SERIAL,
   name VARCHAR(50)  NOT NULL,
   PRIMARY KEY(wear_id),
   UNIQUE(name)
);

CREATE TABLE item(
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
   PRIMARY KEY(item_id),
   FOREIGN KEY(category_id) REFERENCES category(category_id),
   FOREIGN KEY(seller_cip) REFERENCES user_(cip),
   FOREIGN KEY(wear_id) REFERENCES wear(wear_id)
);

CREATE TABLE log_(
   id SERIAL,
   timestamp_ TIMESTAMP NOT NULL,
   content_1 VARCHAR(350)  NOT NULL,
   content_2 VARCHAR(350) ,
   log_category_name VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(log_category_name) REFERENCES log_category(log_category_name)
);

CREATE TABLE image_(
   image_url VARCHAR(50) ,
   item_id INTEGER NOT NULL,
   PRIMARY KEY(image_url),
   FOREIGN KEY(item_id) REFERENCES item(item_id)
);

CREATE TABLE order_(
   order_id SERIAL,
   created_at TIMESTAMP NOT NULL,
   updated_at VARCHAR(50) ,
   quantity VARCHAR(50)  NOT NULL,
   item_id INTEGER NOT NULL,
   buyer_cip VARCHAR(8)  NOT NULL,
   PRIMARY KEY(order_id),
   FOREIGN KEY(item_id) REFERENCES item(item_id),
   FOREIGN KEY(buyer_cip) REFERENCES user_(cip)
);

CREATE TABLE comment_(
   comment_id SERIAL,
   timestamp_ TIMESTAMP NOT NULL,
   content VARCHAR(360)  NOT NULL,
   updated_at TIMESTAMP,
   comment_id_1 INTEGER NOT NULL,
   item_id INTEGER NOT NULL,
   sender_cip VARCHAR(8)  NOT NULL,
   PRIMARY KEY(comment_id),
   FOREIGN KEY(comment_id_1) REFERENCES comment_(comment_id),
   FOREIGN KEY(item_id) REFERENCES item(item_id),
   FOREIGN KEY(sender_cip) REFERENCES user_(cip)
);

CREATE TABLE order_message(
   timestamp_ TIMESTAMP,
   content TEXT NOT NULL,
   is_read BOOLEAN NOT NULL,
   order_id INTEGER NOT NULL,
   sender_cip VARCHAR(8)  NOT NULL,
   PRIMARY KEY(timestamp_),
   FOREIGN KEY(order_id) REFERENCES order_(order_id),
   FOREIGN KEY(sender_cip) REFERENCES user_(cip)
);

CREATE TABLE item_del_option(
   item_id INTEGER,
   delivery_optn_id INTEGER,
   PRIMARY KEY(item_id, delivery_optn_id),
   FOREIGN KEY(item_id) REFERENCES item(item_id),
   FOREIGN KEY(delivery_optn_id) REFERENCES delivery_option(delivery_optn_id)
);

CREATE TABLE tag_item(
   item_id INTEGER,
   tag_id INTEGER,
   PRIMARY KEY(item_id, tag_id),
   FOREIGN KEY(item_id) REFERENCES item(item_id),
   FOREIGN KEY(tag_id) REFERENCES tag(tag_id)
);

CREATE TABLE favorite(
   cip VARCHAR(8) ,
   item_id INTEGER,
   added_at TIMESTAMP NOT NULL,
   PRIMARY KEY(cip, item_id),
   FOREIGN KEY(cip) REFERENCES user_(cip),
   FOREIGN KEY(item_id) REFERENCES item(item_id)
);


INSERT INTO user_ (cip, first_name, last_name, email, is_admin, profile_picture_url, enabled, created_at, updated_at) 
VALUES 
   ('bela3439', 'Alex', 'Bellefroid Lefkakis', 'bela3439@usherbrooke.ca', false, NULL, true, NOW(), NULL),
   ('boum7113', 'Milo', 'Boucher', 'boum7113@usherbrooke.ca', false, NULL, true, NOW(), NULL),
   ('dubw5596', 'William', 'Dubuc', 'dubw5596@usherbrooke.ca', false, NULL, true, NOW(), NULL),
   ('herl2700', 'Léanne', 'Héroux', 'herl2700@usherbrooke.ca', false, NULL, true, NOW(), NULL),
   ('larj4236', 'Jean-Félix', 'Larouche', 'larj4236@usherbrooke.ca', false, NULL, true, NOW(), NULL),
   ('pele3157', 'Éliane', 'Pelletier', 'pele3157@usherbrooke.ca', false, NULL, true, NOW(), NULL),
   ('test1234', 'Utiilisateur', 'Test', 'test1234@usherbrooke.ca', false, NULL, true, NOW(), NULL);

INSERT INTO delivery_option (name) VALUES ('Livraison'), ('À récupérer'), ('Transfert par courriel');

INSERT INTO category (name, parent_category) VALUES ('Vêtements', NULL), ('Électronique', NULL), ('Livres', NULL), ('Maisons', NULL), ('Sports', NULL), ('Autres', NULL), ('Hauts', 1), ('Bas', 1), ('Chaussures', 1), ('Accessoires', 1);

INSERT INTO tag (name) VALUES ('Électronique'), ('Neuf'), ('Cours'), ('Usager');

INSERT INTO log_category (log_category_name) VALUES ('User Actions'), ('Item Management'), ('Orders'), ('Comments'), ('System Events');

INSERT INTO wear (name) VALUES ('Factory New'), ('Minimal Wear'), ('Field-Tested'), ('Well-Worn'), ('Battle-Scarred');

INSERT INTO ebock.item
    (name, description, price, added_at, updated_at, sold, quantity, archived, category_id, wear_id, seller_cip)
VALUES ('Mac Book avec Puce M5', 'MacBook avec une puce M5 qui run linux très bien', 2500.12, now(),
        now(), false, 1, false, 2, 1, 'herl2700'),
    ('Auto BAJA', 'Belle auto baja avec une bonne transmission', 15234.60, now(),
     now(), false, 1, false, 2 , 1, 'boum7113'),
    ('Fusée L1', 'Une belle fusée qui peux être utilisé comme un missile', 1000.00, now(),
     now(), false, 1, false, 2, 1, 'dubw5596'),
    ('Chalk', 'Chalk pour l escalade', 67.67, now(), now(), true, 1,
     false, 5, 1, 'larj4236'),
    ('Prise de laptop', 'Une prise de laptop vraiment longue', 10.00, now(), now(),
     false, 1, false, 2, 1, 'pele3157'),
    ('Lit', 'Pas besoin de lit si je dors pas', 649.00, now(), now(),
     true, 0, true, 4, 1, 'bela3439');
