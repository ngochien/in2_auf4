package de.hawhamburg.bank.services;

import java.util.List;

import de.hawhamburg.bank.persistence.Bank;
import de.hawhamburg.bank.persistence.CardIssuer;

public interface BankService {

	List<Bank> getBanks();

	List<CardIssuer> getCardIssuers();

}
