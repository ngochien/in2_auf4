package de.hawhamburg.bank.services;

import org.junit.Assert;
import org.junit.Test;

public class BankServiceTest {

	private static final String PERSISTENCE_UNIT = "haw_bank";

	@Test
	public void testGetBanks() {
		ApplicationContext.getApplicationContext().initJPA(PERSISTENCE_UNIT);
		BankService bankService = new BankServiceImpl();
		Assert.assertNotNull(bankService.getBanks());
	}

	@Test
	public void testGetCardIssuers() {
		ApplicationContext.getApplicationContext().initJPA(PERSISTENCE_UNIT);
		BankService bankService = new BankServiceImpl();
		Assert.assertNotNull(bankService.getCardIssuers());
	}

}
