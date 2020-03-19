package com.yferhaoui.reservations_ski.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.TreeSet;

import com.jfoenix.controls.JFXComboBox;
import com.yferhaoui.reservations_ski.Popup;
import com.yferhaoui.reservations_ski.data.Booking;
import com.yferhaoui.reservations_ski.data.Equipment;
import com.yferhaoui.reservations_ski.data.EquipmentList;
import com.yferhaoui.reservations_ski.data.Person;
import com.yferhaoui.reservations_ski.data.PersonBooking;
import com.yferhaoui.reservations_ski.data.PersonList;
import com.yferhaoui.reservations_ski.data.base.Database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public final class AddModifyPersonBookingController {

	@FXML
	private JFXComboBox<Person> personComboBox;

	@FXML
	private JFXComboBox<Equipment> equipmentComboBox;

	@FXML
	private DatePicker startDateDatePicker;

	@FXML
	private DatePicker endDateDatePicker;

	// ----------------------- //
	private final Database database;

	private PersonList personList;
	private EquipmentList equipmentList;
	private Booking booking;
	private PersonBooking personBooking;
	private boolean validated = false;

	public AddModifyPersonBookingController(final Database database, //
			final PersonList personList, //
			final EquipmentList equipmentList, //
			final Booking booking, //
			final PersonBooking personBooking) {
		this.database = database;
		this.personList = personList;
		this.equipmentList = equipmentList;
		this.booking = booking;
		this.personBooking = personBooking;
	}

	@FXML
	public final void initialize() {

		final TreeSet<Person> persons = this.personList.getCopyList();
		final TreeSet<Equipment> equipments = this.equipmentList.getCopyList();
		if (this.personBooking != null) {
			persons.remove(this.personBooking.getPerson());
			persons.add(this.personBooking.getPerson());
			equipments.remove(this.personBooking.getEquipment());
			equipments.add(this.personBooking.getEquipment());
		}

		// Persons JFXComboBox
		this.personComboBox.getItems().addAll(persons);

		// Equipments JFXComboBox
		this.equipmentComboBox.getItems().addAll(equipments);

		// If mode modify -> fill fields
		if (this.personBooking != null) {

			this.personComboBox.setValue(this.personBooking.getPerson());
			this.equipmentComboBox.setValue(this.personBooking.getEquipment());
			this.startDateDatePicker.setValue(
					this.personBooking.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			this.endDateDatePicker
					.setValue(this.personBooking.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		}
	}

	// Getter
	public final boolean isValidated() {
		return this.validated;
	}

	// Action Events
	// When the button Cancel is performed
	@FXML
	public final void cancel(final ActionEvent e) throws IOException, InterruptedException {
		final Stage window = ((Stage) ((Node) e.getSource()).getScene().getWindow());
		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	// When the button Validate is performed
	@FXML
	public final void validate(final ActionEvent e) throws IOException, InterruptedException {
		try {

			if (this.personComboBox.getValue() == null) {
				throw new Exception("Please select a Person !");
			}

			if (this.equipmentComboBox.getValue() == null) {
				throw new Exception("Please select an Equipment !");
			}

			if (this.startDateDatePicker.getValue() == null) {
				throw new Exception("Please select a Start Date !");
			}

			if (this.startDateDatePicker.getValue() == null) {
				throw new Exception("Please select a End Date !");
			}

			final LocalDate localStartDate = this.startDateDatePicker.getValue();
			final LocalDate localEndDate = this.endDateDatePicker.getValue();

			if (!localStartDate.isBefore(localEndDate)) {
				throw new Exception("Please select a Start Date which is before the End Date !");
			}

			if (this.personBooking != null) { // Update the Person Booking
				
				this.booking.getPersonBookingList().remove(this.personBooking);
				
				this.database.deletePersonBooking(this.personBooking, booking.getBookingID());

				this.personBooking.setPerson(this.personComboBox.getValue());
				this.personBooking.setEquipment(this.equipmentComboBox.getValue());
				this.personBooking
						.setStartDate(localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
				this.personBooking
						.setEndDate(localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

			} else { // New Person Booking

				this.personBooking = new PersonBooking(this.personComboBox.getValue(), //
						this.equipmentComboBox.getValue(), //
						localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), //
						localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
			}

			if (this.booking != null) { // If booking exist Add/update to it

				this.booking.addPersonBooking(this.personBooking);

			} else { // Else create a new Booking to add it
				final TreeSet<PersonBooking> personBookings = new TreeSet<PersonBooking>();
				personBookings.add(this.personBooking);
				this.booking = new Booking(personBookings);
			}

			this.database.insertOrUpdateBooking(this.booking);
			this.validated = true;

			// Close the current window
			final Window window = ((Stage) ((Node) e.getSource()).getScene().getWindow());
			window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));

		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}
	
	// Getter
	public final Booking getBooking() {
		return this.booking;
	}
	
	public final PersonBooking getPersonBooking() {
		return this.personBooking;
	}
}
