package de.hawhamburg.bank.presentation.customer;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.bank.persistence.Address;
import de.hawhamburg.bank.persistence.Bank;
import de.hawhamburg.bank.persistence.CardIssuer;
import de.hawhamburg.bank.persistence.CreditCard;
import de.hawhamburg.bank.persistence.CreditCardType;
import de.hawhamburg.bank.persistence.Customer;
import de.hawhamburg.bank.presentation.ServiceProvider;
import de.hawhamburg.bank.services.BankService;
import de.hawhamburg.bank.services.CustomerService;

/**
 * Controller for Customer page.
 */
public class CustomerController {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomerController.class);

	@FXML
	private TextField name;

	@FXML
	private TextField postcode;

	@FXML
	private TextField city;

	@FXML
	private TextField street;

	@FXML
	private ChoiceBox<Bank> bank;

	@FXML
	private CheckBox isCC;

	@FXML
	private TextField ccnumber;

	@FXML
	private ChoiceBox<CreditCardType> cctype;

	@FXML
	private TextArea message;

	@FXML
	private void initialize() {
		LOG.info("initializing ...");
		initializeBank();
		initializeCctype();
		LOG.info("... done");
	}

	private void initializeCctype() {
		final ObservableList<CreditCardType> cctypeObs = FXCollections
				.observableArrayList();
		cctypeObs.addAll(CreditCardType.values());
		cctype.setItems(cctypeObs);
	}

	private void initializeBank() {
		bank.setConverter(new StringConverter<Bank>() {

			@Override
			public String toString(final Bank bank) {
				return bank.getName();
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
		banksObs.addAll(banks);
		bank.setItems(banksObs);
	}

	@FXML
	protected void createCustomer(final ActionEvent event) {
		LOG.info("creating customer ...");
		message.setText("");
		try {
			final CustomerService customerService = ServiceProvider
					.getInstance().getCustomerService();
			final Customer customer = buildCustomer();
			customerService.createCustomer(customer);
			message.setText("Customer " + customer.getId() + " created.");
			if (isCC.isSelected()) {
				LOG.debug("isCC selected");
				final CreditCard creditCard = buildCreditCard();
				customerService.addCreditCard(customer, creditCard);
				message.setText(message.getText() + "Credit card "
						+ creditCard.getId() + " created.");
			}
		} catch (final Exception e) {
			LOG.warn("error: " + e.toString(), e);
			message.setText(message.getText() + " " + e.getMessage());
		}
		LOG.info("... done");
	}

	private CreditCard buildCreditCard() {
		final CreditCard creditCard = new CreditCard();
		creditCard.setNumber(ccnumber.getText());
		creditCard.setType(cctype.getValue());
		return creditCard;
	}

	private Customer buildCustomer() {
		final Customer customer = new Customer();
		customer.setName(name.getText());
		final Address address = new Address();
		address.setStreet(street.getText());
		address.setPostcode(postcode.getText());
		address.setCity(city.getText());
		customer.setHomeAddress(address);
		customer.getBanks().add(bank.getValue());
		return customer;
	}

	@FXML
	protected void toggleCC(final ActionEvent event) {
		LOG.info("toggling ...");
		ccnumber.setDisable(!isCC.isSelected());
		cctype.setDisable(!isCC.isSelected());
		LOG.info("... toggled");
	}
}
