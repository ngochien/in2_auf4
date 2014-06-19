package de.hawhamburg.bank.presentation.payments;

import java.util.Date;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import de.hawhamburg.bank.vo.PaymentStatement;

public class PaymentView {

	private PaymentStatement paymentStatement = null;
	private SimpleStringProperty customerName = new SimpleStringProperty("");
	private SimpleStringProperty creditCardNo = new SimpleStringProperty("");
	private SimpleObjectProperty<Date> paymentDate = new SimpleObjectProperty<Date>(
			null);
	private SimpleDoubleProperty paymentAmount = new SimpleDoubleProperty(0.0);

	public PaymentView() {
		super();
	}

	public PaymentView(final String customerName, final String creditCardNo,
			final Date paymentDate, final Double paymentAmount) {
		setCustomerName(customerName);
		setCreditCardNo(creditCardNo);
		setPaymentDate(paymentDate);
		setPaymentAmount(paymentAmount);
	}

	public PaymentView(final PaymentStatement paymentStatement) {
		setPaymentStatement(paymentStatement);
		setCustomerName(paymentStatement.getCustomerSurname() + " "
				+ paymentStatement.getCustomerName());
		setCreditCardNo(paymentStatement.getCreditCardNumber());
		setPaymentDate(paymentStatement.getPaymentDate());
		setPaymentAmount(paymentStatement.getPaymentAmount().doubleValue());
	}

	public String getCustomerName() {
		return customerName.get();
	}

	public void setCustomerName(final String customerName) {
		this.customerName.set(customerName);
	}

	public String getCreditCardNo() {
		return creditCardNo.get();
	}

	public void setCreditCardNo(final String creditCardNo) {
		this.creditCardNo.set(creditCardNo);
	}

	public Date getPaymentDate() {
		return paymentDate.get();
	}

	public void setPaymentDate(final Date paymentDate) {
		this.paymentDate.set(paymentDate);
	}

	public Double getPaymentAmount() {
		return paymentAmount.getValue();
	}

	public void setPaymentAmount(final Double paymentAmount) {
		if (paymentAmount == null) {
			this.paymentAmount.set(0.0);
		} else {
			this.paymentAmount.set(paymentAmount);
		}
	}

	public PaymentStatement getPaymentStatement() {
		return paymentStatement;
	}

	public void setPaymentStatement(final PaymentStatement paymentStatement) {
		this.paymentStatement = paymentStatement;
	}
}
