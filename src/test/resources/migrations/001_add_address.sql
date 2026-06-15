BEGIN;

ALTER TABLE ebock.user_
ADD COLUMN address VARCHAR(150) DEFAULT '2500 Bd de lUniversité, Sherbrooke, QC J1N 3C6';

COMMIT;