ALTER TABLE fr.deposit ADD CONSTRAINT deposit_uniq UNIQUE(deposit_id, event_id);

ALTER TABLE fr.destination ADD CONSTRAINT destination_uniq UNIQUE(destination_id, event_id);

ALTER TABLE fr.identity ADD CONSTRAINT identity_uniq UNIQUE(identity_id, event_id);

ALTER TABLE fr.challenge ADD CONSTRAINT challenge_uniq UNIQUE(challenge_id, identity_id, event_id);

ALTER TABLE fr.source ADD CONSTRAINT source_uniq UNIQUE(source_id, event_id);

ALTER TABLE fr.wallet ADD CONSTRAINT wallet_uniq UNIQUE(wallet_id, event_id);

ALTER TABLE fr.withdrawal ADD CONSTRAINT withdrawal_uniq UNIQUE(withdrawal_id, event_id);
