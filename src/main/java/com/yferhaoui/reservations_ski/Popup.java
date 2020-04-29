package com.yferhaoui.reservations_ski;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class Popup {

	public final static void alert(final Exception e) {
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(e.getClass().getSimpleName());
		alert.setHeaderText(e.getMessage());
		alert.showAndWait();
	}
}
