package de.hawhamburg.bank.services;

import java.util.List;

import de.hawhamburg.bank.persistence.CreditCard;
import de.hawhamburg.bank.persistence.Customer;
import de.hawhamburg.bank.persistence.CustomerFilter;

public interface CustomerService {

	void createCustomer(Customer customer);

	void addCreditCard(Customer customer, CreditCard creditCard);

	List<Customer> selectCustomers(CustomerFilter filter);

	int deleteCustomers(CustomerFilter filter);

}
