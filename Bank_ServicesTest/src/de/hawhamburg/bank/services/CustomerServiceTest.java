package de.hawhamburg.bank.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hawhamburg.bank.persistence.Address;
import de.hawhamburg.bank.persistence.Bank;
import de.hawhamburg.bank.persistence.CardIssuer;
import de.hawhamburg.bank.persistence.CreditCard;
import de.hawhamburg.bank.persistence.CreditCardType;
import de.hawhamburg.bank.persistence.Customer;

public class CustomerServiceTest {

	private static final String PERSISTENCE_UNIT = "haw_bank";

	private static final String SURNAME_1 = "Felix";
	private static final String NAME_1 = "Furchtlos";

	private static final String STREET_1 = "Murmelgasse";

	private static final String POSTCODE_1 = "20000";

	private static final String CITY_1 = "Hamburg";

	private Bank bank;

	private CardIssuer cardIssuer;

	private CustomerService customerService;

	@Before
	public void setUp() {
		ApplicationContext.getApplicationContext().initJPA(PERSISTENCE_UNIT);
		bank = getBank();
		cardIssuer = getCardIssuer();
		customerService = new CustomerServiceImpl();
	}

	@Test
	public void testCreateCustomer() {
		final Customer customer = createCustomer();
		checkCustomer(customer.getName(), null);
	}

	@Test
	public void testAddCreditCard() {
		final Customer customer = createCustomer();
		final CreditCard creditCard = new CreditCard();
		creditCard.setNumber(String.valueOf(System.currentTimeMillis()));
		creditCard.setType(CreditCardType.CREDIT);
		creditCard.setIssuer(cardIssuer);
		customerService.addCreditCard(customer, creditCard);
		checkCustomer(customer.getName(), creditCard);
	}

	private Customer createCustomer() {
		final Customer customer = new Customer();
		final String name = NAME_1 + UUID.randomUUID().toString();
		customer.setName(name);
		customer.setSurname(SURNAME_1);
		final Address address = new Address();
		address.setStreet(STREET_1);
		address.setPostcode(POSTCODE_1);
		address.setCity(CITY_1);
		customer.setHomeAddress(address);
		customer.getBanks().add(bank);
		customerService.createCustomer(customer);
		return customer;
	}

	private void checkCustomer(final String name, final CreditCard creditCard) {
		final EntityManager em = getEntityManager();
		final List<Customer> customers = em
				.createNamedQuery("selectCustomersByName", Customer.class)
				.setParameter("name", name).getResultList();
		Assert.assertEquals(1, customers.size());
		final Customer customer = customers.get(0);
		Assert.assertEquals(name, customer.getName());
		Assert.assertEquals(SURNAME_1, customer.getSurname());
		final Address homeAddress = customer.getHomeAddress();
		Assert.assertNotNull(homeAddress);
		Assert.assertEquals(POSTCODE_1, homeAddress.getPostcode());
		Assert.assertEquals(STREET_1, homeAddress.getStreet());
		Assert.assertEquals(CITY_1, homeAddress.getCity());
		final Set<Bank> banks = customer.getBanks();
		Assert.assertEquals(1, banks.size());
		Assert.assertEquals(bank, banks.iterator().next());
		final Set<CreditCard> creditCards = customer.getCreditCards();
		Assert.assertEquals(creditCard == null ? 0 : 1, creditCards.size());
	}

	private Bank getBank() {
		final BankService bankService = new BankServiceImpl();
		final List<Bank> banks = bankService.getBanks();
		Assert.assertTrue(banks.size() > 0);
		return banks.get(0);
	}

	private CardIssuer getCardIssuer() {
		final BankService bankService = new BankServiceImpl();
		final List<CardIssuer> cardIssuers = bankService.getCardIssuers();
		Assert.assertTrue(cardIssuers.size() > 0);
		return cardIssuers.get(0);
	}

	private EntityManager getEntityManager() {
		return ApplicationContext.getApplicationContext().getEntityManager();
	}

}
