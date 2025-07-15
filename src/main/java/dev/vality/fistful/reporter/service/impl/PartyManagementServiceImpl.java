package dev.vality.fistful.reporter.service.impl;

import dev.vality.damsel.domain.Party;
import dev.vality.damsel.payment_processing.InvalidPartyRevision;
import dev.vality.damsel.payment_processing.PartyManagementSrv;
import dev.vality.damsel.payment_processing.PartyNotFound;
import dev.vality.fistful.reporter.exception.PartyManagementClientException;
import dev.vality.fistful.reporter.exception.PartyNotFoundException;
import dev.vality.fistful.reporter.service.PartyManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyManagementServiceImpl implements PartyManagementService {

    private final PartyManagementSrv.Iface partyManagementClient;

    @Override
    public Party getParty(String partyId) {
        log.info("Trying to get party, partyId='{}'", partyId);
        try {
            Party party = partyManagementClient.get(partyId);
            log.info("Party has been found, partyId='{}'", partyId);
            return party;
        } catch (PartyNotFound ex) {
            throw new PartyNotFoundException(
                    String.format("Party not found, partyId='%s'",
                            partyId),
                    ex);
        } catch (InvalidPartyRevision ex) {
            throw new PartyNotFoundException(
                    String.format("Invalid party revision, partyId='%s'",
                            partyId),
                    ex);
        } catch (TException ex) {
            throw new PartyManagementClientException(
                    String.format("Failed to get party, partyId='%s'",
                            partyId),
                    ex);
        }
    }
}
