package de.hawhamburg.bank.presentation.payments;

import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hawhamburg.bank.presentation.ServiceProvider;
import de.hawhamburg.bank.services.PaymentService;
import de.hawhamburg.bank.vo.PaymentStatement;

/**
 * Controller for payments page.
 */
public class PaymentsController {

	private static final Logger LOG = LoggerFactory
			.getLogger(PaymentsController.class);

	private static final int NUMBER_OF_PAYMENTS_PER_PAGE = 14;

	private int firstPayment = 0;

	@FXML
	private TableView<PaymentView> payments;

	@FXML
	private TableColumn<PaymentView, String> customer;

	@FXML
	private TableColumn<PaymentView, String> creditCard;

	@FXML
	private TableColumn<PaymentView, Date> paymentDate;

	@FXML
	private Button previousPage;

	@FXML
	private Button deleteSelected;

	@FXML
	private TextArea message;

	@FXML
	private void initialize() {
		LOG.info("initializing ...");
		initializePayments();
		initializeColumns();
		selectPayments();
		LOG.info("... done");
	}

	private void initializePayments() {
		final ObservableList<PaymentView> paymentsObs = FXCollections
				.observableArrayList();
		payments.setItems(paymentsObs);
	}

	private void initializeColumns() {
		customer.prefWidthProperty().bind(payments.widthProperty().divide(4));
		creditCard.prefWidthProperty().bind(payments.widthProperty().divide(4));
		paymentDate.prefWidthProperty()
				.bind(payments.widthProperty().divide(4));
	}

	@FXML
	protected void selectPreviousPayments(final ActionEvent event) {
		LOG.info("selecting previous payments ...");
		this.firstPayment -= NUMBER_OF_PAYMENTS_PER_PAGE;
		if (this.firstPayment < 0) {
			this.firstPayment = 0;
		}
		selectPayments();
	}

	@FXML
	protected void deleteSelectedPayment(final ActionEvent event) {
		LOG.info("delete selected payment ...");
		final PaymentView selectedPayment = this.payments.getSelectionModel()
				.getSelectedItem();
		if (selectedPayment == null) {
			LOG.warn("No payment selected");
			return;
		}
		final long id = selectedPayment.getPaymentStatement().getPaymentId();
		final ServiceProvider serviceProvider = ServiceProvider.getInstance();
		final PaymentService paymentService = serviceProvider
				.getPaymentService();
		paymentService.deletePayment(id);
		selectPayments();
	}

	@FXML
	protected void refreshPayments(final ActionEvent event) {
		LOG.info("refresh payments ...");
		selectPayments();
	}
	
	private void selectPayments() {
		final String text1 = "Selecting " + NUMBER_OF_PAYMENTS_PER_PAGE
				+ " payments starting at " + firstPayment + ". ";
		LOG.info(text1);
		message.setText(text1);
		final ServiceProvider serviceProvider = ServiceProvider.getInstance();
		final PaymentService paymentService = serviceProvider
				.getPaymentService();
		final List<PaymentStatement> payments = paymentService.selectPayments(
				firstPayment, NUMBER_OF_PAYMENTS_PER_PAGE);
		showPayments(payments);
		final String text2 = "Found " + payments.size() + " payments.";
		LOG.info(text2);
		message.setText(text1 + text2);
		this.previousPage.setDisable(firstPayment == 0);
	}

	private void showPayments(final List<PaymentStatement> payments) {
		final ObservableList<PaymentView> paymentObs = this.payments.getItems();
		paymentObs.clear();
		for (final PaymentStatement payment : payments) {
			final PaymentView view = new PaymentView(payment);
			paymentObs.add(view);
		}
	}
}
