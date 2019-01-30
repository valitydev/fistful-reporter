package com.rbkmoney.fistful.reporter.service;

import com.rbkmoney.damsel.domain.Contract;
import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.domain.PaymentInstitutionRef;
import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.damsel.payment_processing.PartyRevisionParam;
import com.rbkmoney.fistful.reporter.exception.ContractNotFoundException;
import com.rbkmoney.fistful.reporter.exception.NotFoundException;
import com.rbkmoney.fistful.reporter.exception.PartyNotFoundException;

import java.time.Instant;

public interface PartyManagementService {

    Party getParty(String partyId) throws PartyNotFoundException;

    Party getParty(String partyId, Instant timestamp) throws PartyNotFoundException;

    Party getParty(String partyId, long partyRevision) throws PartyNotFoundException;

    Party getParty(String partyId, PartyRevisionParam partyRevisionParam) throws PartyNotFoundException;

    Contract getContract(String partyId, String contractId) throws ContractNotFoundException, PartyNotFoundException;

    Contract getContract(String partyId, String contractId, long partyRevision) throws ContractNotFoundException, PartyNotFoundException;

    Contract getContract(String partyId, String contractId, Instant timestamp) throws ContractNotFoundException, PartyNotFoundException;

    Contract getContract(String partyId, String contractId, PartyRevisionParam partyRevisionParam) throws ContractNotFoundException, PartyNotFoundException;

    PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId) throws ContractNotFoundException, PartyNotFoundException;

    PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId, long partyRevision) throws ContractNotFoundException, PartyNotFoundException;

    PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId, Instant timestamp) throws ContractNotFoundException, PartyNotFoundException;

    PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId, PartyRevisionParam partyRevisionParam) throws ContractNotFoundException, PartyNotFoundException;

    Value getMetaData(String partyId, String namespace) throws NotFoundException;
}
