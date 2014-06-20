package de.hawhamburg.bank.presentation.customers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.bank.persistence.Bank;
import de.hawhamburg.bank.persistence.CardIssuer;
import de.hawhamburg.bank.persistence.CreditCardType;
import de.hawhamburg.bank.persistence.Customer;
import de.hawhamburg.bank.persistence.CustomerFilter;
import de.hawhamburg.bank.presentation.ServiceProvider;
import de.hawhamburg.bank.services.BankService;
import de.hawhamburg.bank.services.CustomerService;

/**
 * Controller for Selecting Customers page.
 */
public class CustomersController {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomersController.class);

	@FXML
	private TextField name;

	@FXML
	private TextField postcode;

	@FXML
	private ChoiceBox<Bank> bank;

	@FXML
	private ChoiceBox<CreditCardType> cctype;

	@FXML
	private ChoiceBox<CardIssuer> cardIssuer;
	
	@FXML
	private TextArea message;

	@FXML
	private void initialize() {
		LOG.info("initializing ...");
		initializeBank();
		initializeCardIssuer();
		initializeCctype();
		LOG.info("... done");
	}

	private void initializeCctype() {
		cctype.setConverter(new StringConverter<CreditCardType>() {

			@Override
			public String toString(final CreditCardType type) {
				return type == null ? "Select .." : type.toString();
			}

			@Override
			public CreditCardType fromString(final String string) {
				return null;
			}
		});
		final ObservableList<CreditCardType> cctypeObs = FXCollections
				.observableArrayList();
		cctypeObs.add(null);
		cctypeObs.addAll(CreditCardType.values());
		cctype.setItems(cctypeObs);
		cctype.getSelectionModel().select(0);
	}
	
	private void initializeCardIssuer() {
		cardIssuer.setConverter(new StringConverter<CardIssuer>() {

			@Override
			public String toString(final CardIssuer issuer) {
				return issuer == null ? "Select .." : issuer.getName();
			}

			@Override
			public CardIssuer fromString(final String string) {
				return null;
			}
		});
		final ObservableList<CardIssuer> cardIssuerObs = FXCollections
				.observableArrayList();
		final ServiceProvider serviceProvider = ServiceProvider.getInstance();
		final BankService bankService = serviceProvider.getBankService();
		final List<CardIssuer> cardIssuers = bankService.getCardIssuers();
		cardIssuerObs.add(null);
		cardIssuerObs.addAll(cardIssuers);
		cardIssuer.setItems(cardIssuerObs);
		cardIssuer.getSelectionModel().select(0);
	}

	private void initializeBank() {
		bank.setConverter(new StringConverter<Bank>() {

			@Override
			public String toString(final Bank bank) {
				return bank == null ? "Select .." : bank.getName();
			}

			@Override
			public Bank fromString(final String string) {
				return null;
			}
		});
		final ObservableList<Bank> banksObs = FXCollections
				.observableArrayList();
		final ServiceProvider serviceProvider = ServiceProvider.getInstance();
		final BankService bankService = serviceProvider.getBankService();
		final List<Bank> banks = bankService.getBanks();
		banksObs.add(null);
		banksObs.addAll(banks);
		bank.setItems(banksObs);
		bank.getSelectionModel().select(0);
	}

	@FXML
	protected void selectCustomers(final ActionEvent event) {
		LOG.info("selecting customers ...");
		message.setText("");
		final ServiceProvider serviceProvider = ServiceProvider.getInstance();
		final CustomerService customerService = serviceProvider
				.getCustomerService();
		final CustomerFilter filter = buildFilter();
		final List<Customer> customers = customerService
				.selectCustomers(filter);
		message.setText("Found " + customers.size() + " customers");
		LOG.info("... done");
	}

	@FXML
	protected void deleteCustomers(final ActionEvent event) {
		LOG.info("deleting customers ...");
		message.setText("");
		final ServiceProvider serviceProvider = ServiceProvider.getInstance();
		final CustomerService customerService = serviceProvider
				.getCustomerService();
		final CustomerFilter filter = buildFilter();
		final int customersDeleted = customerService.deleteCustomers(filter);
		message.setText("Deleted " + customersDeleted + " customers");
		LOG.info("... done");
	}

	private CustomerFilter buildFilter() {
		final CustomerFilter filter = new CustomerFilter();
		filter.setName(normalize(this.name.getText()));
		filter.setPostcode(normalize(this.postcode.getText()));
		filter.setBank(this.bank.getValue());
		filter.setCcType(this.cctype.getValue());
		filter.setCcIssuer(this.cardIssuer.getValue());
		return filter;
	}

	private String normalize(final String s) {
		if (s == null || s.trim().length() == 0) {
			return null;
		}
		return s.trim();
	}
}
