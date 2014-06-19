package de.hawhamburg.bank.services;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.bank.persistence.CreditCard;
import de.hawhamburg.bank.persistence.Customer;
import de.hawhamburg.bank.persistence.CustomerDAO;
import de.hawhamburg.bank.persistence.CustomerFilter;

public class CustomerServiceImpl implements CustomerService {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomerServiceImpl.class);

	protected CustomerServiceImpl() {
		super();
	}

	public void createCustomer(final Customer customer) {
		LOG.debug(customer != null ? customer.toString() : "customer is null");
		final EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(customer);
		em.getTransaction().commit();
		LOG.debug("Committed");
	}

	@Override
	public void addCreditCard(final Customer customer,
			final CreditCard creditCard) {
		LOG.debug(customer != null ? customer.toString() : "customer is null");
		LOG.debug(creditCard != null ? creditCard.toString()
				: "creditCard is null");
		final EntityManager em = getEntityManager();
		em.getTransaction().begin();
		customer.getCreditCards().add(creditCard);
		creditCard.setHolder(customer);
		em.persist(customer);
		em.persist(creditCard);
		em.getTransaction().commit();
		LOG.debug("Committed");
	}

	@Override
	public List<Customer> selectCustomers(final CustomerFilter filter) {
		final CustomerDAO customerDAO = new CustomerDAO(getEntityManager());
		final List<Customer> customers = customerDAO
				.selectByCustomerFilter(filter);
		return customers;
	}

	@Override
	public int deleteCustomers(final CustomerFilter filter) {
		final EntityManager em = getEntityManager();
		em.getTransaction().begin();
		final CustomerDAO customerDAO = new CustomerDAO(em);
		final List<Customer> customers = customerDAO
				.selectByCustomerFilter(filter);
		for (final Customer customer : customers) {
			em.remove(customer);
		}
		em.getTransaction().commit();
		return customers.size();
	}

	private EntityManager getEntityManager() {
		return ApplicationContext.getApplicationContext().getEntityManager();
	}

}
