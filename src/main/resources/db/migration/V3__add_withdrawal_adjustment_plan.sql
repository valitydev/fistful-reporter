ALTER TYPE fr.withdrawal_event_type ADD VALUE 'WITHDRAWAL_ADJUSTMENT_SUCCEEDED';

CREATE TABLE fr.withdrawal_adjustment_plan (
  adjustment_id CHARACTER VARYING NOT NULL,
  withdrawal_id CHARACTER VARYING NOT NULL,
  amount BIGINT,
  currency_code CHARACTER VARYING,
  fee BIGINT NOT NULL,
  provider_fee BIGINT NOT NULL,
  applied BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT withdrawal_adjustment_plan_pkey PRIMARY KEY (adjustment_id)
);
