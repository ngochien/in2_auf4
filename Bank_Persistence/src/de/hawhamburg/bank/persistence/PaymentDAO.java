package de.hawhamburg.bank.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.bank.vo.PaymentStatement;

public class PaymentDAO {

	private static final Logger LOG = LoggerFactory.getLogger(PaymentDAO.class);

	private EntityManager em;

	public PaymentDAO(final EntityManager em) {
		this.em = em;
	}

	public List<PaymentStatement> selectPaymentStatements(final int from,
			final int count) {
		LOG.debug("selecting payment statements ...");
		final TypedQuery<Payment> query = em.createNamedQuery(
				"selectAllPaymentsOrderedByDate", Payment.class);
		query.setFirstResult(from);
		query.setMaxResults(count);
		final List<Payment> payments = query.getResultList();
		final List<PaymentStatement> result = new ArrayList<PaymentStatement>(
				payments.size());
		for (final Payment payment : payments) {
			final CreditCard creditCard = payment.getCreditCard();
			final Customer customer = creditCard.getHolder();
			final PaymentStatement statement = new PaymentStatement();
			statement.setCustomerId(customer.getId());
			statement.setCustomerName(customer.getName());
			statement.setCustomerSurname(customer.getSurname());
			statement.setCreditCardId(creditCard.getId());
			statement.setCreditCardNumber(creditCard.getNumber());
			statement.setPaymentId(payment.getId());
			statement.setPaymentDate(payment.getDate());
			statement.setPaymentAmount(payment.getAmount());
			result.add(statement);
		}
		LOG.debug("... " + result.size() + " payment statements selected");
		return result;
	}

}
