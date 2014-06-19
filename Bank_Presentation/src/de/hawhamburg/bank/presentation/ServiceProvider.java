package de.hawhamburg.bank.presentation;

import de.hawhamburg.bank.services.BankService;
import de.hawhamburg.bank.services.CustomerService;
import de.hawhamburg.bank.services.PaymentService;
import de.hawhamburg.bank.services.ServiceProviderImpl;

public class ServiceProvider implements
		de.hawhamburg.bank.services.ServiceProvider {

	private de.hawhamburg.bank.services.ServiceProvider bankServiceProvider;

	private static final ServiceProvider SINGLETON = new ServiceProvider();

	public static ServiceProvider getInstance() {
		return SINGLETON;
	}

	private ServiceProvider() {
		super();
	}

	private de.hawhamburg.bank.services.ServiceProvider getBankServiceProvider() {
		if (bankServiceProvider == null) {
			setBankServiceProvider(new ServiceProviderImpl());
		}
		assert bankServiceProvider != null;
		return bankServiceProvider;
	}

	public synchronized void setBankServiceProvider(
			final de.hawhamburg.bank.services.ServiceProvider serviceProvider) {
		if (bankServiceProvider == null) {
			bankServiceProvider = serviceProvider;
		}
	}

	@Override
	public BankService getBankService() {
		return getBankServiceProvider().getBankService();
	}

	@Override
	public CustomerService getCustomerService() {
		return getBankServiceProvider().getCustomerService();
	}

	@Override
	public PaymentService getPaymentService() {
		return getBankServiceProvider().getPaymentService();
	}

}
