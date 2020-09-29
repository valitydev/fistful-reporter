DELETE FROM fr.identity;
ALTER TABLE fr.identity DROP COLUMN sequence_id;

DELETE FROM fr.challenge;
ALTER TABLE fr.challenge DROP COLUMN sequence_id;

DELETE FROM fr.wallet;
ALTER TABLE fr.wallet DROP COLUMN sequence_id;

DELETE FROM fr.source;
ALTER TABLE fr.source DROP COLUMN sequence_id;

DELETE FROM fr.destination;
ALTER TABLE fr.destination DROP COLUMN sequence_id;

DELETE FROM fr.withdrawal;
ALTER TABLE fr.withdrawal DROP COLUMN sequence_id;

DELETE FROM fr.deposit;
ALTER TABLE fr.deposit DROP COLUMN sequence_id;

DELETE FROM fr.fistful_cash_flow;
ALTER TABLE fr.fistful_cash_flow DROP COLUMN sequence_id;


