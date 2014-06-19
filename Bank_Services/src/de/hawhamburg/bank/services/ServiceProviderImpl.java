package de.hawhamburg.bank.services;

public class ServiceProviderImpl implements ServiceProvider {

	private BankService bankService;

	private CustomerService customerService;

	private PaymentService paymentService;

	public ServiceProviderImpl() {
		super();
	}

	@Override
	public BankService getBankService() {
		if (bankService == null) {
			setBankService(new BankServiceImpl());
		}
		assert bankService != null;
		return bankService;
	}

	public synchronized void setBankService(final BankService bankService) {
		synchronized (this) {
			if (this.bankService == null) {
				this.bankService = bankService;
			}
		}
	}

	@Override
	public CustomerService getCustomerService() {
		if (customerService == null) {
			setCustomerService(new CustomerServiceImpl());
		}
		assert customerService != null;
		return customerService;
	}

	public synchronized void setCustomerService(
			final CustomerService customerService) {
		synchronized (this) {
			if (this.customerService == null) {
				this.customerService = customerService;
			}
		}
	}

	@Override
	public PaymentService getPaymentService() {
		if (paymentService == null) {
			setPaymentService(new PaymentServiceImpl());
		}
		assert paymentService != null;
		return paymentService;
	}

	public synchronized void setPaymentService(
			final PaymentService paymentService) {
		synchronized (this) {
			if (this.paymentService == null) {
				this.paymentService = paymentService;
			}
		}
	}

}
