package dev.vality.fistful.reporter.service;

import dev.vality.damsel.domain.Contract;
import dev.vality.damsel.domain.Party;
import dev.vality.damsel.payment_processing.PartyRevisionParam;

public interface PartyManagementService {

    Party getParty(String partyId, PartyRevisionParam partyRevisionParam);

    Contract getContract(String partyId, String contractId);

}
