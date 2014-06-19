package de.hawhamburg.bank.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.bank.persistence.Payment;
import de.hawhamburg.bank.persistence.PaymentDAO;
import de.hawhamburg.bank.vo.PaymentStatement;

public class PaymentServiceImpl implements PaymentService {

	private static final Logger LOG = LoggerFactory
			.getLogger(PaymentServiceImpl.class);

	protected PaymentServiceImpl() {
		super();
	}

	@Override
	public List<PaymentStatement> selectPayments(final int first,
			final int count) {
		LOG.debug("selecting up to " + count + " payments starting at " + first);
		final PaymentDAO paymentDAO = new PaymentDAO(getEntityManager());
		final List<PaymentStatement> result = paymentDAO
				.selectPaymentStatements(first, count);
		return result;
	}

	@Override
	public void deletePayment(long id) {
		LOG.debug("deleting payment " + id);
		final EntityManager em = getEntityManager();
		em.getTransaction().begin();
		final Payment payment = em.find(Payment.class, new Long(id),
				LockModeType.PESSIMISTIC_WRITE);
		if (payment == null) {
			LOG.warn("Payment with ID " + id + " not found");
		} else {
			em.remove(payment);
		}
		em.getTransaction().commit();
	}

	private EntityManager getEntityManager() {
		return ApplicationContext.getApplicationContext().getEntityManager();
	}

}
