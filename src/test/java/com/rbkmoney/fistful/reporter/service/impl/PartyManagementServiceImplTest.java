package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.damsel.domain.Contract;
import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.service.PartyManagementService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;

public class PartyManagementServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private PartyManagementService partyManagementService;

    @MockBean
    private PartyManagementSrv.Iface partyManagementClient;

    private Party party;
    private Contract contract;
    private PartyRevisionParam revision;

    @Before
    public void setUp() throws Exception {
        contract = new Contract();
        contract.setId("0");
        party = new Party();
        party.setId("0");
        party.setContracts(
                new HashMap<>() {{
                    put(contract.getId(), contract);
                }}
        );
        UserInfo userInfo = new UserInfo("fistful-reporter", UserType.internal_user(new InternalUser()));
        revision = PartyRevisionParam.revision(0L);

        Mockito.when(partyManagementClient.getContract(userInfo, party.getId(), contract.getId()))
                .thenReturn(contract);
        Mockito.when(partyManagementClient.checkout(userInfo, party.getId(), revision))
                .thenReturn(party);
    }

    @Test
    public void test() {
        Contract contract = partyManagementService.getContract(this.party.getId(), this.contract.getId(), this.revision);
        Assert.assertEquals(contract, this.contract);
        Party party = partyManagementService.getParty(this.party.getId(), this.revision);
        Assert.assertEquals(party, this.party);
    }
}
