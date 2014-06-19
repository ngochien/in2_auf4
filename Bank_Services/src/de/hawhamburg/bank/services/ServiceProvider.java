package de.hawhamburg.bank.services;

public interface ServiceProvider {

	BankService getBankService();

	CustomerService getCustomerService();

	PaymentService getPaymentService();
}
