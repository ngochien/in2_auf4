package de.hawhamburg.bank.services;

import java.util.List;

import javax.persistence.EntityManager;

import de.hawhamburg.bank.persistence.Bank;
import de.hawhamburg.bank.persistence.CardIssuer;

public class BankServiceImpl implements BankService {

	protected BankServiceImpl() {
	}

	@Override
	public List<Bank> getBanks() {
		final EntityManager em = getEntityManager();
		final List<Bank> banks = em.createNamedQuery("allBanksOrderedByName",
				Bank.class).getResultList();
		return banks;
	}

	@Override
	public List<CardIssuer> getCardIssuers() {
		final EntityManager em = getEntityManager();
		final List<CardIssuer> cardIssuers = em.createNamedQuery(
				"allCardIssuersOrderedByName", CardIssuer.class)
				.getResultList();
		return cardIssuers;
	}

	private EntityManager getEntityManager() {
		return ApplicationContext.getApplicationContext().getEntityManager();
	}

}
