package dev.vality.fistful.reporter.service;

import dev.vality.damsel.domain.Contract;
import dev.vality.damsel.domain.Party;
import dev.vality.damsel.payment_processing.PartyManagementSrv;
import dev.vality.damsel.payment_processing.PartyRevisionParam;
import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class PartyManagementServiceTest {

    @MockBean
    private PartyManagementSrv.Iface partyManagementClient;

    @Autowired
    private PartyManagementService partyManagementService;

    @Test
    public void partyManagementServiceTest() throws TException {
        Contract contract = new Contract();
        contract.setId("0");
        Party party = new Party();
        party.setId("0");

        party.setContracts(Map.of(contract.getId(), contract));
        PartyRevisionParam revision = PartyRevisionParam.revision(0L);

        Mockito.when(partyManagementClient.getContract(party.getId(), contract.getId()))
                .thenReturn(contract);
        Mockito.when(partyManagementClient.checkout(party.getId(), revision))
                .thenReturn(party);

        Contract c = partyManagementService.getContract(party.getId(), contract.getId());
        assertEquals(contract, c);
        Party p = partyManagementService.getParty(party.getId(), revision);
        assertEquals(party, p);
    }
}
