package de.hawhamburg.bank.persistence;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(CreditCard.class)
public class CreditCard_ {

	public static volatile SingularAttribute<CreditCard, Long> id;
	public static volatile SingularAttribute<CreditCard, String> number;
	public static volatile SingularAttribute<CreditCard, CreditCardType> type;
	public static volatile SingularAttribute<CreditCard, CardIssuer> issuer;
	public static volatile SingularAttribute<CreditCard, Customer> holder;

}
