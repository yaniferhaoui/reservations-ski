package com.yferhaoui.reservations_ski.scene;

import java.io.IOException;

import com.yferhaoui.reservations_ski.controller.AddModifyPersonBookingController;
import com.yferhaoui.reservations_ski.data.Booking;
import com.yferhaoui.reservations_ski.data.EquipmentList;
import com.yferhaoui.reservations_ski.data.PersonBooking;
import com.yferhaoui.reservations_ski.data.PersonList;
import com.yferhaoui.reservations_ski.data.base.Database;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class AddModifyPersonBookingWindow extends Stage {

	private final AddModifyPersonBookingController controller;

	public AddModifyPersonBookingWindow(final Database database, //
			final PersonList personList, //
			final EquipmentList equipmentList, //
			final Booking booking, //
			final PersonBooking personBooking) throws IOException {
		this(database, "Modify PersonBooking", personList, equipmentList,  booking, personBooking);
	}

	public AddModifyPersonBookingWindow(final Database database, //
			final PersonList personList, //
			final EquipmentList equipmentList, //
			final Booking booking) throws IOException {
		this(database, "Add PersonBooking", personList, equipmentList, booking, null);
	}

	public AddModifyPersonBookingWindow(final Database database, //
			final PersonList personList, //
			final EquipmentList equipmentList) throws IOException {
		this(database, "Add PersonBooking", personList, equipmentList,  null, null);
	}

	private AddModifyPersonBookingWindow(final Database database, //
			final String title, //
			final PersonList personList, //
			final EquipmentList equipmentList, //
			final Booking booking, //
			final PersonBooking personBooking) throws IOException {

		this.controller = new AddModifyPersonBookingController(database, personList, equipmentList, booking, personBooking);
		final FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getClassLoader().getResource("fxml/AddModifyPersonBooking.fxml"));
		fxmlLoader.setController(this.controller);

		final Scene scene = new Scene(fxmlLoader.load());
		
		// Load all css
		scene.getStylesheets().addAll(//
				this.getClass().getClassLoader().getResource("css/fonts.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/material-color.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/skeleton.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/light.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/bootstrap.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/shape.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/typographic.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/helpers.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/master.css").toExternalForm());
		this.setScene(scene);
		this.setTitle(title);
		this.setResizable(false);
	}

	// Getter
	public final boolean isValidated() {
		return this.controller.isValidated();
	}
	
	public final Booking getBooking() {
		return this.controller.getBooking();
	}
	
	public final PersonBooking getPersonBooking() {
		return this.controller.getPersonBooking();
	}
}
