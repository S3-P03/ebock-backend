BEGIN;

ALTER TABLE ebock.user_
DROP COLUMN IF EXISTS address;

CREATE TABLE ebock.province(
   province_code VARCHAR(2),
   province_name VARCHAR(30) NOT NULL,
   PRIMARY KEY(province_code),
   UNIQUE(province_name)
);

CREATE TABLE ebock.address(
	address_id SERIAL,
	civic_number INT NOT NULL,
	appt_number INT,
	street VARCHAR(60) NOT NULL,
	postal_code VARCHAR(7) NOT NULL,
	country VARCHAR(30) NOT NULL,
	province_code VARCHAR(2) NOT NULL,
	PRIMARY KEY(address_id),
	FOREIGN KEY(province_code) REFERENCES ebock.province(province_code)
);

INSERT INTO ebock.province(province_name, province_code) 
VALUES 
('Alberta', 'AB'), 
('Colombie-Britannique', 'BC'), 
('Manitoba', 'MB'), 
('Nouveau-Brunswick', 'NB'), 
('Terre-Neuve-et-Labrador', 'NL'), 
('Territoires du Nord-Ouest', 'NT'), 
('Nouvelle-Écosse', 'NS'), 
('Nunavut', 'NU'), 
('Ontario', 'ON'), 
('île-du-Prince-Édouard', 'PE'), 
('Québec', 'QC'), 
('Saskatchewan', 'SK'), 
('Yukon', 'YT');

INSERT INTO ebock.address(civic_number, street, postal_code, country, province_code)
VALUES (2500, 'Bd de lUniversité', 'J1N 3C6', 'Canada', 'QC');

ALTER TABLE ebock.user_
ADD COLUMN address_id INT NOT NULL DEFAULT 1;

ALTER TABLE ebock.user_
ADD CONSTRAINT user_address
FOREIGN KEY (address_id)
REFERENCES ebock.address (address_id);

COMMIT;