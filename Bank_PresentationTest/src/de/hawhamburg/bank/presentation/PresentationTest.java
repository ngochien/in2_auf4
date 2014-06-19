package de.hawhamburg.bank.presentation;


import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.bank.persistence.Address;
import de.hawhamburg.bank.persistence.Bank;
import de.hawhamburg.bank.persistence.CardIssuer;
import de.hawhamburg.bank.persistence.CreditCard;
import de.hawhamburg.bank.persistence.CreditCardType;
import de.hawhamburg.bank.persistence.Customer;
import de.hawhamburg.bank.persistence.Payment;
import de.hawhamburg.se.TransactionManager;

/**
 * Vor dem Test muss die Datenbank mit dem Skript sql/createdb.sql erzeugt
 * worden sein.
 * 
 */
public class PresentationTest {

	// Die folgenden drei Konstanten m�ssen jeweils angepasst werden:
	private static final String DB_URL =
			"jdbc:oracle:thin:@ora5.informatik.haw-hamburg.de:1521:inf09";
	private static final String DB_USER = "abl400";
	private static final String DB_PASSWORD = "Nh31011991";
	// Ende

	private static final Logger LOG = LoggerFactory
			.getLogger(PresentationTest.class);

	private static final String PERSISTENCE_UNIT = "haw_bank";

	private static final String POSTCODE1 = "20000";
	private static final String POSTCODE2 = "28000";

	private static final String BANK_1 = "Money & More";
	private static final String BANK_2 = "Less Money";

	private static final String CARD_ISSUER_1 = "Mister";
	private static final String CARD_ISSUER_2 = "Vasi";

	private static TransactionManager transactionManager;

	private static EntityManagerFactory emf = null;
	private static EntityManager em = null;

	@org.junit.BeforeClass
	public static void setUpClass() throws SQLException, InterruptedException {
		transactionManager = new TransactionManager(DB_URL);
		transactionManager.connect(DB_USER, DB_PASSWORD);
		deleteTestData();
		deleteMasterData();
		createEntityManager();
		createMasterData();
		LOG.info("Starting application");
		new Thread() {
			@Override
			public void run() {
				Banking.main(new String[] {});
			}

		}.start();
		// Wait for startup
		Thread.sleep(2 * 1000);
	}

	@org.junit.AfterClass
	public static void tearDownClass() {
		LOG.info("Stopping application");
		Platform.exit();
		if (em != null) {
			try {
				em.close();
			} catch (final Throwable ex) {
				LOG.warn("while closing entity manager", ex);
			} finally {
				em = null;
			}
		}
		if (emf != null) {
			try {
				emf.close();
			} catch (final Throwable ex) {
				LOG.warn("while closing entity manager factory", ex);
			} finally {
				emf = null;
			}
		}
		transactionManager.disconnect();
		transactionManager = null;
	}

	@org.junit.Before
	public void setUp() throws SQLException {
		deleteTestData();
	}

	@org.junit.After
	public void tearDown() throws SQLException {
	}

	private static void deleteTestData() throws SQLException {
		transactionManager.executeSQLDeleteOrUpdate("delete from PAYMENT",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate(
				"delete from BANK_CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CREDITCARD",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from ADDRESS a where not exists(select * from BANK_OFFICE bo where bo.ADDRESS_ID = a.ID)",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.commit();
	}

	private static void deleteMasterData() throws SQLException {
		transactionManager.executeSQLDeleteOrUpdate("delete from BANK_OFFICE",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from BANK",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CARDISSUER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from ADDRESS",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.commit();
	}

	private static void createEntityManager() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		em = emf.createEntityManager();
	}

	private static void createMasterData() {
		em.getTransaction().begin();
		final Address bank_1_office_1 = new Address(POSTCODE1, "HH",
				"Murmelgasse");
		final Address bank_1_office_2 = new Address(POSTCODE2, "HB",
				"Heinzelsta�e");

		final Bank bank1 = new Bank(BANK_1);
		bank1.getOffices().add(bank_1_office_1);
		bank1.getOffices().add(bank_1_office_2);

		final Bank bank2 = new Bank(BANK_2);

		final CardIssuer cardIssuer_1 = new CardIssuer(CARD_ISSUER_1);
		final CardIssuer cardIssuer_2 = new CardIssuer(CARD_ISSUER_2);

		em.persist(cardIssuer_1);
		em.persist(cardIssuer_2);
		em.persist(bank1);
		em.persist(bank2);
		em.getTransaction().commit();
	}

	/**
	 * Erg�nze die Datei {@link de.hawhamburg.bank.presentation.customer.customer
	 * -tab.fxml} und die Klasse
	 * {@link de.hawhamburg.bank.presentation.customer.CustomerController}, so dass
	 * der Test {@link #testCreateCustomerData()} gr�n wird.
	 * 
	 * Es soll interaktiv �ber die Oberfl�che ein Kunde mit einer Kreditkarte
	 * angelegt werden.
	 */
	@org.junit.Test
	public void testCreateCustomerData() throws SQLException, IOException,
	InterruptedException {
		System.out
		.println("---> Hit <ENTER> in this window when a customer with credit card was entered");
		System.in.read(new byte[100]);
		checkCustomer();
	}

	private void checkCustomer() {
		final List<Customer> customers = em.createNamedQuery(
				"selectAllCustomers", Customer.class).getResultList();
		Assert.assertTrue(customers.size() > 0);
		for (final Customer customer : customers) {
			LOG.info(customer.toString());
			Assert.assertNotNull(customer.getName());
			Assert.assertNotNull(customer.getSurname());
			Assert.assertNotNull(customer.getHomeAddress());
			Assert.assertNotNull(customer.getHomeAddress().getPostcode());
			Assert.assertNotNull(customer.getHomeAddress().getCity());
			Assert.assertNotNull(customer.getHomeAddress().getStreet());
			Assert.assertEquals(1, customer.getBanks().size());
			Assert.assertEquals(1, customer.getCreditCards().size());
			for (final CreditCard creditCard : customer.getCreditCards()) {
				LOG.info(creditCard.toString());
				Assert.assertNotNull(creditCard.getNumber());
				Assert.assertNotNull(creditCard.getType());
				Assert.assertNotNull(creditCard.getIssuer());
			}
		}
	}


	/**
	 * Erg�nze die Datei {@link de.hawhamburg.bank.presentation.customer.customers
	 * -tab.fxml} und die Klasse
	 * {@link de.hawhamburg.bank.presentation.customers.CustomersController}, so
	 * dass der Test {@link #testDeleteCustomerData()} gr�n wird.
	 * 
	 * Es soll interaktiv �ber die Oberfl�che ein bestimmter Kunde herausgefiltert
	 * und dann gel�scht werden.
	 */
	@org.junit.Test
	public void testDeleteCustomerData() throws SQLException,
	InterruptedException, IOException {
		createCustomerData();
		final int numberOfCustomers = em.createNamedQuery("selectAllCustomers")
				.getResultList().size();
		final String bankToBeDeleted = Math.random() > 0.5 ? BANK_1 : BANK_2;
		final CreditCardType creditCardTypeToBeDeleted = Math.random() > 0.5 ? CreditCardType.CREDIT
				: CreditCardType.DEBIT;
		final String cardIssuerToBeDeleted = Math.random() > 0.5 ? CARD_ISSUER_1
				: CARD_ISSUER_2;

		System.out
		.println("---> Hit <ENTER> in this window when customer of bank "
				+ bankToBeDeleted
				+ " with a "
				+ creditCardTypeToBeDeleted
				+ " card from "
				+ cardIssuerToBeDeleted + " was deleted");
		System.in.read(new byte[100]);

		// Check: number of customers is one less:
		Assert.assertEquals(numberOfCustomers - 1,
				em.createNamedQuery("selectAllCustomers").getResultList()
				.size());
		// Check that the right one is missing:
		TypedQuery<Customer> query = em
				.createQuery(
						"select c from Customer c join c.banks b join c.creditCards cc where b.name = :bankName and cc.type = :ccType and cc.issuer.name = :ccIssuerName",
						Customer.class);
		query = query.setParameter("bankName", bankToBeDeleted)
				.setParameter("ccType", creditCardTypeToBeDeleted)
				.setParameter("ccIssuerName", cardIssuerToBeDeleted);
		final List<Customer> customers = query.getResultList();
		Assert.assertEquals("Customers found: " + customers, 0,
				customers.size());
	}

	private void createCustomerData() {
		em.getTransaction().begin();
		final TypedQuery<Bank> bankQuery = em.createQuery(
				"select b from Bank b where b.name = ?", Bank.class);
		final Bank bank1 = bankQuery.setParameter(1, BANK_1).getSingleResult();
		final Bank bank2 = bankQuery.setParameter(1, BANK_2).getSingleResult();

		final TypedQuery<CardIssuer> cardIssuerQuery = em
				.createQuery("select c from CardIssuer c where c.name = ?",
						CardIssuer.class);
		final CardIssuer cardIssuer_1 = cardIssuerQuery.setParameter(1,
				CARD_ISSUER_1).getSingleResult();
		final CardIssuer cardIssuer_2 = cardIssuerQuery.setParameter(1,
				CARD_ISSUER_2).getSingleResult();

		int i = 0;
		createCustomer(i++, bank1, CreditCardType.CREDIT, cardIssuer_1);
		createCustomer(i++, bank1, CreditCardType.CREDIT, cardIssuer_2);
		createCustomer(i++, bank1, CreditCardType.DEBIT, cardIssuer_1);
		createCustomer(i++, bank1, CreditCardType.DEBIT, cardIssuer_2);
		createCustomer(i++, bank2, CreditCardType.CREDIT, cardIssuer_1);
		createCustomer(i++, bank2, CreditCardType.CREDIT, cardIssuer_2);
		createCustomer(i++, bank2, CreditCardType.DEBIT, cardIssuer_1);
		createCustomer(i++, bank2, CreditCardType.DEBIT, cardIssuer_2);

		em.getTransaction().commit();
	}

	private void createCustomer(final int i, final Bank bank,
			final CreditCardType cctype, final CardIssuer ccissuer) {
		final Address customerHome = new Address("20000", "HH",
				"Ost-West-Stra�e " + i);

		final Customer customer = new Customer("Rudi_" + i, "Ratlos");
		customer.setHomeAddress(customerHome);

		final CreditCard creditCard = new CreditCard(String.valueOf(i), cctype);
		creditCard.setIssuer(ccissuer);

		customer.getCreditCards().add(creditCard);
		creditCard.setHolder(customer);

		customer.getBanks().add(bank);

		em.persist(customer);
	}


	/**
	 * Erg�nze die Datei {@link de.hawhamburg.bank.presentation.payments.payments
	 * -tab.fxml} und die Klasse
	 * {@link de.hawhamburg.bank.presentation.payments.PaymentsController}, so dass
	 * der Test {@link #testCreateCustomerData()} gr�n wird.
	 * 
	 * Es soll interaktiv �ber die Oberfl�che eine Zahlung �ber eine ungew�hnlich
	 * hohe Summe gefunden und gel�scht werden.
	 */
	@org.junit.Test
	public void testDeleteWrongPayment() throws SQLException,
	InterruptedException, IOException {
		createCustomerData();
		createPaymentData();
		createWorongPayment();

		System.out
		.println("---> Hit <ENTER> in this window when payment with amount of 100000000 was deleted");
		System.in.read(new byte[100]);

		final List<Payment> payments100000000 = em.createQuery(
				"from Payment where amount = 100000000", Payment.class)
				.getResultList();
		Assert.assertTrue(payments100000000.toString(),
				payments100000000.isEmpty());
	}

	private void createPaymentData() {
		final long startDate = new Date().getTime();
		final TypedQuery<CreditCard> creditCardQuery = em.createQuery(
				"from CreditCard", CreditCard.class);
		final List<CreditCard> creditCards = creditCardQuery.getResultList();

		for (final CreditCard creditCard : creditCards) {
			em.getTransaction().begin();
			for (int i = 0; i < 10; i++) {
				final Payment payment = new Payment();
				payment.setCreditCard(creditCard);
				payment.setDate(new Date(startDate
						+ Math.round((Math.random() * 1000000))));
				payment.setAmount(new BigDecimal(
						Math.round(Math.random() * 10000)));
				payment.setDetails("Was ich immer schon haben wollte");
				em.persist(payment);
			}
			em.getTransaction().commit();
		}
	}

	private void createWorongPayment() {
		em.getTransaction().begin();
		final int indexOfWrongPayment = 19 + (int) (Math
				.round(Math.random() * 60.0));
		final TypedQuery<Payment> creditCardQuery = em.createQuery(
				"from Payment p order by p.date", Payment.class).setFirstResult(indexOfWrongPayment).setMaxResults(1);
		final Payment payment = creditCardQuery.getSingleResult();
		payment.setAmount(new BigDecimal(100000000));
		em.getTransaction().commit();
	}


}
