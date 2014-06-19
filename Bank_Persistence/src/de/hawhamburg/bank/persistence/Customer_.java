package de.hawhamburg.bank.persistence;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(Customer.class)
public class Customer_ {

	public static volatile SingularAttribute<Customer, Long> id;
	public static volatile SingularAttribute<Customer, String> name;
	public static volatile SingularAttribute<Customer, String> surname;
	public static volatile SingularAttribute<Customer, Address> homeAddress;
	public static volatile SetAttribute<Customer, Bank> banks;
	public static volatile SetAttribute<Customer, CreditCard> creditCards;

}
