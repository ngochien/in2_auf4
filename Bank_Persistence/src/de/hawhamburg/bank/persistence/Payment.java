package de.hawhamburg.bank.persistence;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

@Entity
@NamedQueries({ @NamedQuery(name = "selectAllPaymentsOrderedByDate", query = "select p from Payment p inner join fetch p.creditCard c inner join fetch c.holder h order by p.date") })
public class Payment {

	private long id;
	private CreditCard creditCard;
	private Date date;
	private BigDecimal amount;
	private String details;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENTGEN")
	@SequenceGenerator(name = "PAYMENTGEN", sequenceName = "PAYMENTSEQ")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "CREDITCARD_ID")
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	@Column(name = "PAYMENTDATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String toString() {
		return "Payment[id=" + getId() + ", date=" + getDate() + ", amount="
				+ getAmount() + ", details=" + getDetails() + ", creditCard="
				+ getCreditCard().getNumber() + "]";
	}
}
