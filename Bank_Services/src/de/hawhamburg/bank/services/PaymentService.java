package de.hawhamburg.bank.services;

import java.util.List;

import de.hawhamburg.bank.vo.PaymentStatement;

public interface PaymentService {

	/**
	 * Select the payments from number {@code first} (starting at 0), up to
	 * {@code count} payments.
	 * 
	 * @param first
	 *            The first payment to select (starting at 0)
	 * @param count
	 *            The number of payments to select.
	 * @return List of payments.
	 */
	List<PaymentStatement> selectPayments(int first, int count);

	/**
	 * Delete the payment with <code>id</code>.
	 * 
	 * @param id
	 *            ID of the payment to be deleted.
	 */
	void deletePayment(long id);
}
