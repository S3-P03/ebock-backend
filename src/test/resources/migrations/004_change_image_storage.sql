BEGIN;

DROP TABLE IF EXISTS ebock.image_;

CREATE TABLE IF NOT EXISTS  ebock.image_(
	guid varchar(50),
	original_filename varchar(200) NOT NULL,
	file_extension varchar(6),
	created_at timestamp,
	PRIMARY KEY(guid)
);

CREATE TABLE IF NOT EXISTS ebock.item_image(
	item_id int,
	guid VARCHAR(50),
	displayOrder smallint,
	PRIMARY KEY(item_id, guid, displayOrder),
	FOREIGN KEY(item_id) REFERENCES ebock.item(item_id),
	FOREIGN KEY(guid) REFERENCES ebock.image_(guid)
);

ALTER TABLE ebock.user_
RENAME COLUMN profile_picture_url TO profile_picture_guid;

ALTER TABLE ebock.user_
ADD CONSTRAINT profile_picture
FOREIGN KEY (profile_picture_guid)
REFERENCES ebock.image_ (guid);

COMMIT;