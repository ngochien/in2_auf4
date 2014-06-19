package de.hawhamburg.bank.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerDAO {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomerDAO.class);

	private EntityManager em;

	public CustomerDAO(final EntityManager em) {
		this.em = em;
	}

	public List<Customer> selectByCustomerFilter(
			final CustomerFilter customerFilter) {
		LOG.debug(customerFilter.toString());
		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Customer> criteriaQuery = criteriaBuilder
				.createQuery(Customer.class);
		final Root<Customer> root = criteriaQuery.from(Customer.class);
		final List<Predicate> predicates = new ArrayList<Predicate>();
		if (customerFilter.getName() != null) {
			predicates.add(criteriaBuilder.like(root.get(Customer_.name),
					customerFilter.getName()));
		}
		if (customerFilter.getPostcode() != null) {
			predicates.add(criteriaBuilder.like(root.get(Customer_.homeAddress)
					.get(Address_.postcode), customerFilter.getPostcode()));
		}
		if (customerFilter.getBank() != null) {
			predicates.add(criteriaBuilder.isMember(customerFilter.getBank(),
					root.get(Customer_.banks)));
		}
		if (customerFilter.getCcType() != null
				|| customerFilter.getCcIssuer() != null) {
			final Subquery<CreditCard> subquery = criteriaQuery
					.subquery(CreditCard.class);
			final Root<Customer> customerCorrelationRoot = subquery
					.correlate(root);
			final Join<Customer, CreditCard> customerCreditCardCorrelationJoin = customerCorrelationRoot
					.join(Customer_.creditCards);
			subquery.select(customerCreditCardCorrelationJoin);
			final List<Predicate> subqueryPredicates = new ArrayList<Predicate>();
			if (customerFilter.getCcType() != null) {
				subqueryPredicates
						.add(criteriaBuilder.equal(
								customerCreditCardCorrelationJoin
										.get(CreditCard_.type), customerFilter
										.getCcType()));
			}
			if (customerFilter.getCcIssuer() != null) {
				subqueryPredicates.add(criteriaBuilder.equal(
						customerCreditCardCorrelationJoin
								.get(CreditCard_.issuer), customerFilter
								.getCcIssuer()));
			}
			if (subqueryPredicates.size() == 1) {
				subquery.where(subqueryPredicates.get(0));
			} else {
				subquery.where(criteriaBuilder.and(subqueryPredicates
						.toArray(new Predicate[subqueryPredicates.size()])));
			}
			predicates.add(criteriaBuilder.exists(subquery));
		}
		if (!predicates.isEmpty()) {
			if (predicates.size() == 1) {
				criteriaQuery.where(predicates.get(0));
			} else {
				criteriaQuery.where(criteriaBuilder.and(predicates
						.toArray(new Predicate[predicates.size()])));
			}
		}
		final List<Customer> customers = em.createQuery(criteriaQuery)
				.getResultList();
		LOG.debug("Customers found: " + customers.size());
		return customers;
	}
}
