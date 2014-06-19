package de.hawhamburg.bank.presentation.payments;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class FormattedTableCellFactory<S, T> implements
		Callback<TableColumn<S, T>, TableCell<S, T>> {

	private static enum FormatDescription {
		CURRENCY, DATE_TIME
	}

	private TextAlignment alignment;
	private Format format;
	private String formatDescription;

	public TextAlignment getAlignment() {
		return alignment;
	}

	public void setAlignment(TextAlignment alignment) {
		this.alignment = alignment;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String getFormatDescription() {
		return formatDescription;
	}

	public void setFormatDescription(String formatDescription) {
		this.formatDescription = formatDescription;
		if (FormatDescription.CURRENCY.name().equals(formatDescription)) {
			this.setFormat(NumberFormat.getCurrencyInstance());
		} else if (FormatDescription.DATE_TIME.name().equals(formatDescription)) {
			this.setFormat(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public TableCell<S, T> call(TableColumn<S, T> p) {

		TableCell<S, T> cell = new TableCell<S, T>() {
			@Override
			public void updateItem(Object item, boolean empty) {
				if (item == getItem()) {
					return;
				}
				super.updateItem((T) item, empty);
				if (item == null) {
					super.setText(null);
					super.setGraphic(null);
				} else if (format != null) {
					super.setText(format.format(item));
				} else if (item instanceof Node) {
					super.setText(null);
					super.setGraphic((Node) item);
				} else {
					super.setText(item.toString());
					super.setGraphic(null);
				}
			}
		};
		cell.setTextAlignment(alignment);
		switch (alignment) {
		case CENTER:
			cell.setAlignment(Pos.CENTER);
			break;
		case RIGHT:
			cell.setAlignment(Pos.CENTER_RIGHT);
			break;
		default:
			cell.setAlignment(Pos.CENTER_LEFT);
			break;
		}
		return cell;
	}
}
