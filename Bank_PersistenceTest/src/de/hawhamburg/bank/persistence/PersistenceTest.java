package de.hawhamburg.bank.persistence;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.se.TransactionManager;

/**
 * Vor dem Test muss die Datenbank mit dem Skript sql/createdb.sql erzeugt
 * worden sein.
 */
public class PersistenceTest {

	// Die folgenden drei Konstanten m�ssen jeweils angepasst werden:
	private static final String DB_URL =
			"jdbc:oracle:thin:@ora5.informatik.haw-hamburg.de:1521:inf09";
	private static final String DB_USER = "abl400";
	private static final String DB_PASSWORD = "Nh31011991";
	// Ende

	private static final Logger LOG = LoggerFactory
			.getLogger(PersistenceTest.class);

	private static final String PERSISTENCE_UNIT = "haw_bank";

	private static final String SURNAME_1 = "Felix";
	private static final String NAME_1 = "Furchtlos";
	private static final String SURNAME_2 = "Rudi";
	private static final String NAME_2 = "Ratlos";

	private static final String STREET_1 = "Murmelgasse";

	private static final String POSTCODE1 = "20000";
	private static final String POSTCODE2 = "28000";

	private static final String CREDITCARDNO_1 = "1234567890123456";
	private static final String CREDITCARDNO_2 = "0123456789012345";

	private static final String BANK_1 = "Money & More";
	private static final String BANK_2 = "Less Money";

	private static final String CARD_ISSUER_1 = "Mister";
	private static final String CARD_ISSUER_2 = "Vasi";

	private static TransactionManager transactionManager;

	private EntityManagerFactory emf = null;
	private EntityManager em = null;

	@org.junit.BeforeClass
	public static void setUpClass() throws SQLException {
		transactionManager = new TransactionManager(DB_URL);
		transactionManager.connect(DB_USER, DB_PASSWORD);
	}

	@org.junit.AfterClass
	public static void tearDownClass() {
		transactionManager.disconnect();
		transactionManager = null;
	}

	@org.junit.Before
	public void setUp() throws SQLException {
		transactionManager.executeSQLDeleteOrUpdate("delete from PAYMENT",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate(
				"delete from BANK_CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from BANK_OFFICE",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CREDITCARD",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from BANK",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CARDISSUER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from ADDRESS",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.commit();
	}

	@org.junit.After
	public void tearDown() throws SQLException {
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
	}

	private void createEntityManager() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		em = emf.createEntityManager();
	}

	private void createMasterData() {
		em.getTransaction().begin();
		final Address bank_1_office_1 = new Address(POSTCODE1, "HH", STREET_1);
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

	private void createCustomerData() {
		em.getTransaction().begin();
		final Address customer_1_home = new Address(POSTCODE1, "HH",
				"Ost-West-Stra�e");
		final Address customer_2_home = new Address(POSTCODE2, "HB",
				"Nord-S�d-Stra�e");

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

		final Customer customer1 = new Customer(SURNAME_1, NAME_1);
		customer1.setHomeAddress(customer_1_home);

		final Customer customer2 = new Customer(SURNAME_2, NAME_2);
		customer2.setHomeAddress(customer_2_home);

		final CreditCard creditCard1 = new CreditCard(CREDITCARDNO_1,
				CreditCardType.CREDIT);
		creditCard1.setIssuer(cardIssuer_1);

		final CreditCard creditCard2 = new CreditCard(CREDITCARDNO_2,
				CreditCardType.DEBIT);
		creditCard2.setIssuer(cardIssuer_2);

		customer1.getCreditCards().add(creditCard1);
		creditCard1.setHolder(customer1);

		customer1.getCreditCards().add(creditCard2);
		creditCard2.setHolder(customer1);

		customer1.getBanks().add(bank1);
		customer1.getBanks().add(bank2);

		customer2.getBanks().add(bank2);

		em.persist(customer1);
		em.persist(customer2);
		em.getTransaction().commit();
	}

	private void createPaymentData() {
		em.getTransaction().begin();

		final TypedQuery<CreditCard> creditCardQuery = em
				.createQuery(
						"select cc from Customer c join c.creditCards cc where c.name = ?1 and cc.type = 'CREDIT'",
						CreditCard.class);
		final CreditCard creditCard = creditCardQuery.setParameter(1, NAME_1)
				.getSingleResult();

		for (int i = 0; i < 10; i++) {
			final Payment payment = new Payment();
			payment.setCreditCard(creditCard);
			payment.setDate(new Date());
			payment.setAmount(new BigDecimal("123.45"));
			payment.setDetails("Was ich immer schon haben wollte");
			em.persist(payment);
		}
		em.getTransaction().commit();
	}

	@org.junit.Test
	public void testEntityManager() throws SQLException {
		createEntityManager();
		em.getTransaction().begin();
		em.getTransaction().commit();
	}

	@org.junit.Test
	public void testCreateMasterData() throws SQLException {
		createEntityManager();
		createMasterData();
	}

	@org.junit.Test
	public void testCreateCustomerData() throws SQLException {
		createEntityManager();
		createMasterData();
		createCustomerData();
	}

	@org.junit.Test
	public void testCreatePaymentData() throws SQLException {
		createEntityManager();
		createMasterData();
		createCustomerData();
		createPaymentData();
	}

	@org.junit.Test
	public void testCustomerDAO() throws SQLException {
		createEntityManager();
		createMasterData();
		createCustomerData();

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

		final CustomerDAO customerDAO = new CustomerDAO(em);
		final CustomerFilter customerFilter = new CustomerFilter();
		Assert.assertEquals(2,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setName(NAME_2);
		Assert.assertEquals(1,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setPostcode(POSTCODE1);
		Assert.assertEquals(0,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setName(null);
		Assert.assertEquals(1,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setBank(bank1);
		Assert.assertEquals(1,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setBank(bank2);
		Assert.assertEquals(1,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setPostcode(null);
		Assert.assertEquals(2,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setCcType(CreditCardType.CREDIT);
		Assert.assertEquals(1,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setCcIssuer(cardIssuer_2);
		Assert.assertEquals(0,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setCcIssuer(cardIssuer_1);
		Assert.assertEquals(1,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		// Wildcards
		customerFilter.reset();
		final String prefix = NAME_1.substring(0, NAME_1.length() - 2);
		customerFilter.setName(prefix);
		Assert.assertEquals(0,
				customerDAO.selectByCustomerFilter(customerFilter).size());
		customerFilter.setName(prefix + "%");
		Assert.assertEquals(1,
				customerDAO.selectByCustomerFilter(customerFilter).size());
	}

	@org.junit.Test
	public void testPaymentDAO() throws SQLException {
		createEntityManager();
		createMasterData();
		createCustomerData();
		createPaymentData();

		final PaymentDAO paymentDAO = new PaymentDAO(em);

		Assert.assertEquals(10, paymentDAO.selectPaymentStatements(0, 100)
				.size());
		Assert.assertEquals(1, paymentDAO.selectPaymentStatements(0, 1).size());
		Assert.assertEquals(2, paymentDAO.selectPaymentStatements(1, 2).size());

	}
}
