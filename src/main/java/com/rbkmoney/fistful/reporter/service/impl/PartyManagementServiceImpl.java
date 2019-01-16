package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.damsel.domain.Contract;
import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.domain.PaymentInstitutionRef;
import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.fistful.reporter.exception.ContractNotFoundException;
import com.rbkmoney.fistful.reporter.exception.NotFoundException;
import com.rbkmoney.fistful.reporter.exception.PartyNotFoundException;
import com.rbkmoney.fistful.reporter.service.PartyManagementService;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyManagementServiceImpl implements PartyManagementService {

    private final UserInfo userInfo = new UserInfo("fistful-reporter", UserType.internal_user(new InternalUser()));

    private final PartyManagementSrv.Iface partyManagementClient;

    @Override
    public Party getParty(String partyId) throws PartyNotFoundException {
        return getParty(partyId, Instant.now());
    }

    @Override
    public Party getParty(String partyId, Instant timestamp) throws PartyNotFoundException {
        return getParty(partyId, PartyRevisionParam.timestamp(TypeUtil.temporalToString(timestamp)));
    }

    @Override
    public Party getParty(String partyId, long partyRevision) throws PartyNotFoundException {
        return getParty(partyId, PartyRevisionParam.revision(partyRevision));
    }

    @Override
    public Party getParty(String partyId, PartyRevisionParam partyRevisionParam) throws PartyNotFoundException {
        log.info("Trying to get party, partyId='{}', partyRevisionParam='{}'", partyId, partyRevisionParam);
        try {
            Party party = partyManagementClient.checkout(userInfo, partyId, partyRevisionParam);
            log.info("Party has been found, partyId='{}', partyRevisionParam='{}'", partyId, partyRevisionParam);
            return party;
        } catch (PartyNotFound ex) {
            throw new PartyNotFoundException(
                    String.format("Party not found, partyId='%s', partyRevisionParam='%s'", partyId, partyRevisionParam), ex
            );
        } catch (InvalidPartyRevision ex) {
            throw new PartyNotFoundException(
                    String.format("Invalid party revision, partyId='%s', partyRevisionParam='%s'", partyId, partyRevisionParam), ex
            );
        } catch (TException ex) {
            throw new RuntimeException(
                    String.format("Failed to get party, partyId='%s', partyRevisionParam='%s'", partyId, partyRevisionParam), ex
            );
        }
    }

    @Override
    public Contract getContract(String partyId, String contractId) throws ContractNotFoundException, PartyNotFoundException {
        log.info("Trying to get contract, partyId='{}', contractId='{}'", partyId, contractId);
        try {
            Contract contract = partyManagementClient.getContract(userInfo, partyId, contractId);
            log.info("Contract has been found, partyId='{}', contractId='{}'", partyId, contractId);
            return contract;
        } catch (PartyNotFound ex) {
            throw new PartyNotFoundException(String.format("Party not found, partyId='%s'", partyId), ex);
        } catch (ContractNotFound ex) {
            throw new ContractNotFoundException(String.format("Contract not found, partyId='%s', contractId='%s'", partyId, contractId));
        } catch (TException ex) {
            throw new RuntimeException(String.format("Failed to get contract, partyId='%s', contractId='%s'", partyId, contractId), ex);
        }
    }

    @Override
    public Contract getContract(String partyId, String contractId, long partyRevision) throws ContractNotFoundException, PartyNotFoundException {
        return getContract(partyId, contractId, PartyRevisionParam.revision(partyRevision));
    }

    @Override
    public Contract getContract(String partyId, String contractId, Instant timestamp) throws ContractNotFoundException, PartyNotFoundException {
        return getContract(partyId, contractId, PartyRevisionParam.timestamp(TypeUtil.temporalToString(timestamp)));
    }

    @Override
    public Contract getContract(String partyId, String contractId, PartyRevisionParam partyRevisionParam) throws ContractNotFoundException, PartyNotFoundException {
        log.info("Trying to get contract, partyId='{}', contractId='{}', partyRevisionParam='{}'", partyId, contractId, partyRevisionParam);
        Party party = getParty(partyId, partyRevisionParam);

        Contract contract = party.getContracts().get(contractId);
        if (contract == null) {
            throw new ContractNotFoundException(String.format("Contract not found, partyId='%s', contractId='%s', partyRevisionParam='%s'", partyId, contractId, partyRevisionParam));
        }
        log.info("Contract has been found, partyId='{}', contractId='{}', partyRevisionParam='{}'", partyId, contractId, partyRevisionParam);
        return contract;
    }

    @Override
    public PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId) throws ContractNotFoundException, PartyNotFoundException {
        return getPaymentInstitutionRef(partyId, contractId, Instant.now());
    }

    @Override
    public PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId, long partyRevision) throws ContractNotFoundException, PartyNotFoundException {
        return getPaymentInstitutionRef(partyId, contractId, PartyRevisionParam.revision(partyRevision));
    }

    @Override
    public PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId, Instant timestamp) throws ContractNotFoundException, PartyNotFoundException {
        return getPaymentInstitutionRef(partyId, contractId, PartyRevisionParam.timestamp(TypeUtil.temporalToString(timestamp)));
    }

    @Override
    public PaymentInstitutionRef getPaymentInstitutionRef(String partyId, String contractId, PartyRevisionParam partyRevisionParam) throws ContractNotFoundException, PartyNotFoundException {
        log.debug("Trying to get paymentInstitutionRef, partyId='{}', contractId='{}', partyRevisionParam='{}'", partyId, contractId, partyRevisionParam);
        Contract contract = getContract(partyId, contractId, partyRevisionParam);

        if (!contract.isSetPaymentInstitution()) {
            throw new NotFoundException(String.format("PaymentInstitutionRef not found, partyId='%s', contractId='%s', partyRevisionParam='%s'", partyId, contractId, partyRevisionParam));
        }

        PaymentInstitutionRef paymentInstitutionRef = contract.getPaymentInstitution();
        log.info("PaymentInstitutionRef has been found, partyId='{}', contractId='{}', paymentInstitutionRef='{}', partyRevisionParam='{}'", partyId, contractId, paymentInstitutionRef, partyRevisionParam);
        return paymentInstitutionRef;
    }

    @Override
    public Value getMetaData(String partyId, String namespace) throws NotFoundException {
        try {
            return partyManagementClient.getMetaData(userInfo, partyId, namespace);
        } catch (PartyMetaNamespaceNotFound ex) {
            return null;
        } catch (PartyNotFound ex) {
            throw new NotFoundException(
                    String.format("Party not found, partyId='%s', namespace='%s'", partyId, namespace),
                    ex
            );
        } catch (TException ex) {
            throw new RuntimeException(
                    String.format("Failed to get namespace, partyId='%s', namespace='%s'", partyId, namespace), ex
            );
        }
    }
}
