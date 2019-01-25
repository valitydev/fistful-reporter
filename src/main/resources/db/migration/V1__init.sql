CREATE SCHEMA IF NOT EXISTS fr;

-- identity

CREATE TYPE fr.identity_event_type AS ENUM (
  'IDENTITY_CREATED', 'IDENTITY_LEVEL_CHANGED', 'IDENTITY_CHALLENGE_CREATED',
  'IDENTITY_CHALLENGE_STATUS_CHANGED', 'IDENTITY_EFFECTIVE_CHALLENGE_CHANGED'
);

CREATE TABLE fr.identity (
  id                             BIGSERIAL                   NOT NULL,
  event_id                       BIGINT                      NOT NULL,
  event_created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  identity_id                    CHARACTER VARYING           NOT NULL,
  sequence_id                    INT                         NOT NULL,
  event_occured_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type                     fr.identity_event_type      NOT NULL,
  party_id                       CHARACTER VARYING           NOT NULL,
  identity_provider_id           CHARACTER VARYING           NOT NULL,
  identity_class_id              CHARACTER VARYING           NOT NULL,
  party_contract_id              CHARACTER VARYING,
  identity_level_id              CHARACTER VARYING,
  identity_challenge_id          CHARACTER VARYING,
  identity_effective_chalenge_id CHARACTER VARYING,
  wtime                          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT identity_pkey PRIMARY KEY (id)
);

CREATE INDEX identity_event_id_idx
  on fr.identity (event_id);
CREATE INDEX identity_event_created_at_idx
  on fr.identity (event_created_at);
CREATE INDEX identity_id_idx
  on fr.identity (identity_id);
CREATE INDEX identity_event_occured_at_idx
  on fr.identity (event_occured_at);
CREATE INDEX identity_party_id_idx
  on fr.identity (party_id);
CREATE INDEX identity_party_contract_id_idx
  on fr.identity (party_contract_id);

-- challenge

CREATE TYPE fr.challenge_event_type AS ENUM ('CHALLENGE_CREATED', 'CHALLENGE_STATUS_CHANGED');

CREATE TYPE fr.challenge_status AS ENUM ('pending', 'cancelled', 'completed', 'failed');

CREATE TYPE fr.challenge_resolution AS ENUM ('approved', 'denied');

CREATE TABLE fr.challenge (
  id                    BIGSERIAL                   NOT NULL,
  event_id              BIGINT                      NOT NULL,
  event_created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  identity_id           CHARACTER VARYING           NOT NULL,
  sequence_id           INT                         NOT NULL,
  event_occured_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type            fr.challenge_event_type     NOT NULL,
  challenge_id          CHARACTER VARYING           NOT NULL,
  challenge_class_id    CHARACTER VARYING           NOT NULL,
  challenge_status      fr.challenge_status         NOT NULL,
  challenge_resolution  fr.challenge_resolution,
  challenge_valid_until TIMESTAMP WITHOUT TIME ZONE,
  wtime                 TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current               BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT challenge_pkey PRIMARY KEY (id)
);

CREATE INDEX challenge_event_id_idx
  on fr.challenge (event_id);
CREATE INDEX challenge_event_created_at_idx
  on fr.challenge (event_created_at);
CREATE INDEX challenge_identity_id_idx
  on fr.identity (identity_id);
CREATE INDEX challenge_event_occured_at_idx
  on fr.challenge (event_occured_at);
CREATE INDEX challenge_id_idx
  on fr.challenge (challenge_id);

-- withdrawal

CREATE TYPE fr.withdrawal_event_type AS ENUM (
  'WITHDRAWAL_CREATED', 'WITHDRAWAL_STATUS_CHANGED', 'WITHDRAWAL_TRANSFER_CREATED',
  'WITHDRAWAL_TRANSFER_STATUS_CHANGED', 'WITHDRAWAL_SESSION_CHANGED',
  'WITHDRAWAL_ROUTE_CHANGED'
);

CREATE TYPE fr.withdrawal_status AS ENUM ('pending', 'succeeded', 'failed');

CREATE TYPE fr.withdrawal_transfer_status AS ENUM ('created', 'prepared', 'committed', 'cancelled');

CREATE TABLE fr.withdrawal (
  id                         BIGSERIAL                   NOT NULL,
  event_id                   BIGINT                      NOT NULL,
  event_created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  withdrawal_id              CHARACTER VARYING           NOT NULL,
  sequence_id                INT                         NOT NULL,
  event_occured_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type                 fr.withdrawal_event_type    NOT NULL,
  wallet_id                  CHARACTER VARYING           NOT NULL,
  destination_id             CHARACTER VARYING           NOT NULL,
  amount                     BIGINT                      NOT NULL,
  currency_code              CHARACTER VARYING           NOT NULL,
  withdrawal_status          fr.withdrawal_status        NOT NULL,
  withdrawal_transfer_status fr.withdrawal_transfer_status,
  session_id                 CHARACTER VARYING,
  provider_id                CHARACTER VARYING,
  fee                        BIGINT,
  provider_fee               BIGINT,
  wtime                      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                    BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT withdrawal_pkey PRIMARY KEY (id)
);

CREATE INDEX withdrawal_event_id_idx
  on fr.withdrawal (event_id);
CREATE INDEX withdrawal_event_created_at_idx
  on fr.withdrawal (event_created_at);
CREATE INDEX withdrawal_id_idx
  on fr.withdrawal (withdrawal_id);
CREATE INDEX withdrawal_event_occured_at_idx
  on fr.withdrawal (event_occured_at);
CREATE INDEX withdrawal_wallet_id_idx
  on fr.withdrawal (wallet_id);

-- cash flow

CREATE TYPE fr.fistful_cash_flow_change_type AS ENUM ('withdrawal', 'deposit');

CREATE TYPE fr.fistful_cash_flow_account AS ENUM ('merchant', 'provider', 'system', 'external', 'wallet');

CREATE TABLE fr.fistful_cash_flow (
  id                             BIGSERIAL                        NOT NULL,
  event_id                       BIGINT                           NOT NULL,
  event_created_at               TIMESTAMP WITHOUT TIME ZONE      NOT NULL,
  source_id                      CHARACTER VARYING                NOT NULL,
  sequence_id                    INT                              NOT NULL,
  event_occured_at               TIMESTAMP WITHOUT TIME ZONE      NOT NULL,
  event_type                     CHARACTER VARYING                NOT NULL,
  obj_id                         BIGINT                           NOT NULL,
  source_account_type            fr.fistful_cash_flow_account     NOT NULL,
  source_account_type_value      CHARACTER VARYING                NOT NULL,
  source_account_id              CHARACTER VARYING                NOT NULL,
  destination_account_type       fr.fistful_cash_flow_account     NOT NULL,
  destination_account_type_value CHARACTER VARYING                NOT NULL,
  destination_account_id         CHARACTER VARYING                NOT NULL,
  amount                         BIGINT                           NOT NULL,
  currency_code                  CHARACTER VARYING                NOT NULL,
  details                        CHARACTER VARYING,
  obj_type                       fr.fistful_cash_flow_change_type NOT NULL,
  wtime                          TIMESTAMP WITHOUT TIME ZONE      NOT NULL DEFAULT (now() at time zone 'utc'),
  current                        BOOLEAN                          NOT NULL DEFAULT TRUE,
  CONSTRAINT fistful_cash_flow_pkey PRIMARY KEY (id)
);

CREATE INDEX fistful_cash_flow_event_id_idx
  on fr.fistful_cash_flow (event_id);
CREATE INDEX fistful_cash_flow_event_created_at_idx
  on fr.fistful_cash_flow (event_created_at);
CREATE INDEX fistful_cash_flow_source_id_idx
  on fr.fistful_cash_flow (source_id);
CREATE INDEX fistful_cash_flow_event_occured_at_idx
  on fr.fistful_cash_flow (event_occured_at);
CREATE INDEX fistful_cash_flow_obj_id_idx
  on fr.fistful_cash_flow (obj_id);

-- wallet

CREATE TYPE fr.wallet_event_type AS ENUM ('WALLET_CREATED', 'WALLET_ACCOUNT_CREATED');

CREATE TABLE fr.wallet (
  id                   BIGSERIAL                   NOT NULL,
  event_id             BIGINT                      NOT NULL,
  event_created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  wallet_id            CHARACTER VARYING           NOT NULL,
  sequence_id          INT                         NOT NULL,
  event_occured_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type           fr.wallet_event_type        NOT NULL,
  wallet_name          CHARACTER VARYING           NOT NULL,
  party_id             CHARACTER VARYING,
  account_id           CHARACTER VARYING,
  identity_id          CHARACTER VARYING,
  currency_code        CHARACTER VARYING,
  accounter_account_id BIGINT,
  wtime                TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current              BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT wallet_pkey PRIMARY KEY (id)
);

CREATE INDEX wallet_event_id_idx
  on fr.wallet (event_id);
CREATE INDEX wallet_event_created_at_idx
  on fr.wallet (event_created_at);
CREATE INDEX wallet_id_idx
  on fr.wallet (wallet_id);
CREATE INDEX wallet_event_occured_at_idx
  on fr.wallet (event_occured_at);
CREATE INDEX wallet_party_id_idx
  on fr.wallet (party_id);

-- source

CREATE TYPE fr.source_event_type AS ENUM (
  'SOURCE_CREATED', 'SOURCE_ACCOUNT_CREATED', 'SOURCE_STATUS_CHANGED'
);

CREATE TYPE fr.source_status AS ENUM ('authorized', 'unauthorized');

CREATE TABLE fr.source (
  id                        BIGSERIAL                   NOT NULL,
  event_id                  BIGINT                      NOT NULL,
  event_created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  source_id                 CHARACTER VARYING           NOT NULL,
  sequence_id               INT                         NOT NULL,
  event_occured_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type                fr.source_event_type        NOT NULL,
  source_name               CHARACTER VARYING           NOT NULL,
  resource_internal_details CHARACTER VARYING,
  account_id                CHARACTER VARYING,
  identity_id               CHARACTER VARYING,
  currency_code             CHARACTER VARYING,
  accounter_account_id      BIGINT,
  party_id                  CHARACTER VARYING,
  source_status             fr.source_status            NOT NULL,
  wtime                     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                   BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT source_pkey PRIMARY KEY (id)
);

CREATE INDEX source_event_id_idx
  on fr.source (event_id);
CREATE INDEX source_event_created_at_idx
  on fr.source (event_created_at);
CREATE INDEX source_id_idx
  on fr.source (source_id);
CREATE INDEX source_event_occured_at_idx
  on fr.source (event_occured_at);

-- destination

CREATE TYPE fr.destination_event_type AS ENUM (
  'DESTINATION_CREATED', 'DESTINATION_ACCOUNT_CREATED', 'DESTINATION_STATUS_CHANGED'
);

CREATE TYPE fr.destination_status AS ENUM ('authorized', 'unauthorized');

CREATE TABLE fr.destination (
  id                                BIGSERIAL                   NOT NULL,
  event_id                          BIGINT                      NOT NULL,
  event_created_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  destination_id                    CHARACTER VARYING           NOT NULL,
  sequence_id                       INT                         NOT NULL,
  event_occured_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type                        fr.destination_event_type   NOT NULL,
  destination_name                  CHARACTER VARYING           NOT NULL,
  resource_bank_card_token          CHARACTER VARYING,
  resource_bank_card_payment_system CHARACTER VARYING,
  resource_bank_card_bin            CHARACTER VARYING,
  resource_bank_card_masked_pan     CHARACTER VARYING,
  account_id                        CHARACTER VARYING,
  identity_id                       CHARACTER VARYING,
  currency_code                     CHARACTER VARYING,
  accounter_account_id              BIGINT,
  party_id                          CHARACTER VARYING,
  destination_status                fr.destination_status       NOT NULL,
  wtime                             TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                           BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT destination_pkey PRIMARY KEY (id)
);

CREATE INDEX destination_event_id_idx
  on fr.destination (event_id);
CREATE INDEX destination_event_created_at_idx
  on fr.destination (event_created_at);
CREATE INDEX destination_id_idx
  on fr.destination (destination_id);
CREATE INDEX destination_event_occured_at_idx
  on fr.destination (event_occured_at);

-- deposit

CREATE TYPE fr.deposit_event_type AS ENUM (
  'DEPOSIT_CREATED', 'DEPOSIT_STATUS_CHANGED', 'DEPOSIT_TRANSFER_CREATED',
  'DEPOSIT_TRANSFER_STATUS_CHANGED'
);

CREATE TYPE fr.deposit_status AS ENUM ('pending', 'succeeded', 'failed');

CREATE TYPE fr.deposit_transfer_status AS ENUM ('created', 'prepared', 'committed', 'cancelled');

CREATE TABLE fr.deposit (
  id                      BIGSERIAL                   NOT NULL,
  event_id                BIGINT                      NOT NULL,
  event_created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  deposit_id              CHARACTER VARYING           NOT NULL,
  sequence_id             INT                         NOT NULL,
  event_occured_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type              fr.deposit_event_type       NOT NULL,
  wallet_id               CHARACTER VARYING           NOT NULL,
  source_id               CHARACTER VARYING           NOT NULL,
  amount                  BIGINT                      NOT NULL,
  currency_code           CHARACTER VARYING           NOT NULL,
  deposit_status          fr.deposit_status           NOT NULL,
  deposit_transfer_status fr.deposit_transfer_status,
  fee                     BIGINT,
  provider_fee            BIGINT,
  wtime                   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                 BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT deposit_pkey PRIMARY KEY (id)
);

CREATE INDEX deposit_event_id_idx
  on fr.deposit (event_id);
CREATE INDEX deposit_event_created_at_idx
  on fr.deposit (event_created_at);
CREATE INDEX deposit_id_idx
  on fr.deposit (deposit_id);
CREATE INDEX deposit_event_occured_at_idx
  on fr.deposit (event_occured_at);
CREATE INDEX deposit_wallet_id_idx
  on fr.deposit (wallet_id);

-- report

CREATE TYPE fr.REPORT_STATUS AS ENUM ('pending', 'created', 'cancelled');

CREATE TABLE fr.report (
  id          BIGSERIAL                   NOT NULL,
  party_id    CHARACTER VARYING           NOT NULL,
  contract_id CHARACTER VARYING           NOT NULL,
  from_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  to_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  type        CHARACTER VARYING           NOT NULL,
  status      fr.REPORT_STATUS            NOT NULL DEFAULT 'pending' :: fr.REPORT_STATUS,
  timezone    CHARACTER VARYING           NOT NULL,
  CONSTRAINT report_pkey PRIMARY KEY (id)
);

CREATE TABLE fr.file_info (
  id           BIGSERIAL         NOT NULL,
  report_id    BIGINT            NOT NULL,
  file_data_id CHARACTER VARYING NOT NULL,
  CONSTRAINT file_info_pkey PRIMARY KEY (id),
  CONSTRAINT file_info_fkey FOREIGN KEY (report_id) REFERENCES fr.report (id) ON DELETE RESTRICT ON UPDATE NO ACTION
)
