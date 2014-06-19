package de.hawhamburg.bank.vo;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentStatement {

	private long customerId;
	private String customerName;
	private String customerSurname;

	private long creditCardId;
	private String creditCardNumber;

	private long paymentId;
	private Date paymentDate;
	private BigDecimal paymentAmount;

	public PaymentStatement() {
		super();
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(final long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(final String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerSurname() {
		return customerSurname;
	}

	public void setCustomerSurname(final String customerSurname) {
		this.customerSurname = customerSurname;
	}

	public long getCreditCardId() {
		return creditCardId;
	}

	public void setCreditCardId(final long creditCardId) {
		this.creditCardId = creditCardId;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(final String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(final long paymentId) {
		this.paymentId = paymentId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(final Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(final BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

}
