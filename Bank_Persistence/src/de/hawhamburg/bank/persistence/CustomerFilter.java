package de.hawhamburg.bank.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * A filter for selecting customers.
 */
public class CustomerFilter {

	private String name;

	private String postcode;

	private Bank bank;

	private CardIssuer ccIssuer;

	private CreditCardType ccType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public CardIssuer getCcIssuer() {
		return ccIssuer;
	}

	public void setCcIssuer(CardIssuer ccIssuer) {
		this.ccIssuer = ccIssuer;
	}

	public CreditCardType getCcType() {
		return ccType;
	}

	public void setCcType(CreditCardType ccType) {
		this.ccType = ccType;
	}

	public void reset() {
		this.bank = null;
		this.ccIssuer = null;
		this.ccType = null;
		this.name = null;
		this.postcode = null;
	}

	public String toString() {
		final Map<String, String> criteria = new HashMap<String, String>();
		if (getName() != null)
			criteria.put("name", getName());
		if (getPostcode() != null)
			criteria.put("postcode", getPostcode());
		if (getBank() != null)
			criteria.put("bank", getBank().getName());
		if (getCcType() != null)
			criteria.put("ccType", getCcType().toString());
		if (getCcIssuer() != null)
			criteria.put("ccIssuer", getCcIssuer().getName());
		return "CustomerFilter" + criteria;
	}
}
