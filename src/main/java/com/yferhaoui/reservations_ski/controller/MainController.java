package com.yferhaoui.reservations_ski.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.yferhaoui.basic.helper.TimeHelper;
import com.yferhaoui.reservations_ski.Popup;
import com.yferhaoui.reservations_ski.data.Booking;
import com.yferhaoui.reservations_ski.data.Equipment;
import com.yferhaoui.reservations_ski.data.Gender;
import com.yferhaoui.reservations_ski.data.Person;
import com.yferhaoui.reservations_ski.data.PersonBooking;
import com.yferhaoui.reservations_ski.data.PersonList;
import com.yferhaoui.reservations_ski.data.Equipment.EquipmentEnum;
import com.yferhaoui.reservations_ski.data.EquipmentList;
import com.yferhaoui.reservations_ski.data.base.Database;
import com.yferhaoui.reservations_ski.scene.AddModifyEquipmentWindow;
import com.yferhaoui.reservations_ski.scene.AddModifyPersonBookingWindow;
import com.yferhaoui.reservations_ski.scene.AddModifyPersonWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public final class MainController {

	// Number of item per page (Pagination)
	private final int NB_PER_PAGE = 7;

	@FXML
	private Pagination bookingPagination;

	@FXML
	private Pagination personsPagination;

	@FXML
	private Pagination equipmentsPagination;

	// ------------------------------- //

	private final Database database;
	private final PersonList personList;
	private final EquipmentList equipmentList;

	public MainController(final Database database, final PersonList personList, EquipmentList equipmentList) {
		this.database = database;
		this.personList = personList;
		this.equipmentList = equipmentList;
	}

	@FXML
	public final void initialize() throws SQLException {

		this.updateBookingPagination();
		this.updatePersonPagination();
		this.updateEquipmentPagination();
	}

	// To update Booking pages
	private final void updateBookingPagination() throws SQLException {
		final TreeSet<Booking> bookings = this.database.getAllBookings();

		this.bookingPagination
				.setPageCount((bookings.size() / NB_PER_PAGE) + (bookings.size() % NB_PER_PAGE == 0 ? 0 : 1));

		this.bookingPagination.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public final Node call(final Integer pageIndex) {
				final VBox mainVBox = new VBox(10);
				mainVBox.setFillWidth(true);

				final int from = Math.min(Math.max(0, bookings.size() - 1), pageIndex * NB_PER_PAGE);
				final int to = Math.min(Math.max(0, bookings.size()), (pageIndex * NB_PER_PAGE) + NB_PER_PAGE);

				final Booking[] page = Arrays.copyOfRange(bookings.toArray(new Booking[bookings.size()]), from, to);

				// Iterate all booking of the current page
				for (final Booking booking : page) {

					try {

						final FXMLLoader fxmlLoader = new FXMLLoader(
								MainController.class.getClassLoader().getResource("fxml/Booking.fxml"));
						final BorderPane bookingPane = fxmlLoader.load();

						MainController.this.buildBookingBorderPane(booking, bookingPane);

						mainVBox.getChildren().add(bookingPane);
						mainVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));

					} catch (final Exception e) {
						e.printStackTrace();
						Popup.alert(e);
					}
				}

				mainVBox.setFillWidth(true);
				return mainVBox;
			}
		});
	}

	// To update Person pages
	private final void updatePersonPagination() throws SQLException {
		final TreeSet<Person> persons = this.database.getAllPersons();
		this.personsPagination
				.setPageCount((persons.size() / NB_PER_PAGE) + (persons.size() % NB_PER_PAGE == 0 ? 0 : 1));

		this.personsPagination.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public final Node call(final Integer pageIndex) {
				final VBox mainVBox = new VBox(10);
				mainVBox.setFillWidth(true);

				final int from = Math.min(Math.max(0, persons.size() - 1), pageIndex * NB_PER_PAGE);
				final int to = Math.min(Math.max(0, persons.size()), (pageIndex * NB_PER_PAGE) + NB_PER_PAGE);

				final Person[] page = Arrays.copyOfRange(persons.toArray(new Person[persons.size()]), from, to);

				// Iterate all persons of the current page
				for (final Person person : page) {

					try {
						final FXMLLoader fxmlLoader = new FXMLLoader(
								MainController.class.getClassLoader().getResource("fxml/Person.fxml"));
						final GridPane personPane = fxmlLoader.load();

						MainController.this.buildPersonGridPane(person, personPane);

						// To modify the person
						((Button) personPane.lookup("#modifyJFXButton")).setOnAction(event -> {

							try {

								// Open new AddModifyPersonWindow with the fields filled
								final AddModifyPersonWindow stage = new AddModifyPersonWindow(
										MainController.this.database, person);
								stage.initModality(Modality.APPLICATION_MODAL);
								stage.showAndWait();

								if (stage.isValidated()) { // Just update fields

									MainController.this.updatePersonPagination();
									MainController.this.updateBookingPagination();

								}
							} catch (final Exception ex) {
								ex.printStackTrace();
								Popup.alert(ex);
							}

						});

						// To delete the person
						((Button) personPane.lookup("#deleteJFXButton")).setOnAction(event -> {

							try {

								final Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Confirmation Dialog");
								alert.setHeaderText("Deletion of the Person " + person);
								alert.setContentText("Are you sure to delete it?");

								// Ask confirmation
								final Optional<ButtonType> result = alert.showAndWait();
								if (result.get() == ButtonType.OK) {

									MainController.this.personList.remove(person);
									MainController.this.database.deletePerson(person);
									MainController.this.updatePersonPagination();
									MainController.this.updateBookingPagination();
								}

							} catch (final Exception ex) {
								ex.printStackTrace();
								Popup.alert(ex);
							}

						});

						mainVBox.getChildren().add(personPane);
						mainVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));

					} catch (final Exception e) {
						e.printStackTrace();
						Popup.alert(e);
					}
				}

				mainVBox.setFillWidth(true);
				return mainVBox;
			}
		});

	}

	// To update Equipment pages
	private final void updateEquipmentPagination() throws SQLException {
		final TreeSet<Equipment> equipments = this.database.getAllEquipments();
		this.equipmentsPagination
				.setPageCount((equipments.size() / NB_PER_PAGE) + (equipments.size() % NB_PER_PAGE == 0 ? 0 : 1));

		this.equipmentsPagination.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public final Node call(final Integer pageIndex) {
				final VBox mainVBox = new VBox(10);
				mainVBox.setFillWidth(true);

				final int from = Math.min(Math.max(0, equipments.size() - 1), pageIndex * NB_PER_PAGE);
				final int to = Math.min(Math.max(0, equipments.size()), (pageIndex * NB_PER_PAGE) + NB_PER_PAGE);

				final Equipment[] page = Arrays.copyOfRange(equipments.toArray(new Equipment[equipments.size()]), from,
						to);

				// Iterate all equipments of the current page
				for (final Equipment equipment : page) {

					try {
						final FXMLLoader fxmlLoader = new FXMLLoader(
								MainController.class.getClassLoader().getResource("fxml/Equipment.fxml"));
						final GridPane equipmentPane = fxmlLoader.load();

						MainController.this.buildEquipmentGridPane(equipment, equipmentPane);

						// To modify the equipment
						final Button modifyButton = ((Button) equipmentPane.lookup("#modifyJFXButton"));
						modifyButton.setOnAction(event -> {

							try {

								// Open new AddModifyEquipmentWindow with the fields filled
								final AddModifyEquipmentWindow stage = new AddModifyEquipmentWindow(
										MainController.this.database, equipment);
								stage.initModality(Modality.APPLICATION_MODAL);
								stage.showAndWait();

								if (stage.isValidated()) { // Just update field
									MainController.this.updateEquipmentPagination();
									MainController.this.updateBookingPagination();
								}

							} catch (final Exception ex) {
								Popup.alert(ex);
								ex.printStackTrace();
							}

						});

						// To delete the equipment
						final Button deleteButton = ((Button) equipmentPane.lookup("#deleteJFXButton"));
						deleteButton.setOnAction(event -> {

							try {

								final Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Confirmation Dialog");
								alert.setHeaderText("Deletion of the Equipment " + equipment);
								alert.setContentText("Are you sure to delete it?");

								// Ask confirmation
								final Optional<ButtonType> result = alert.showAndWait();
								if (result.get() == ButtonType.OK) {

									MainController.this.equipmentList.remove(equipment);
									MainController.this.database.deleteEquipment(equipment);
									MainController.this.updateEquipmentPagination();
									MainController.this.updateBookingPagination();
								}

							} catch (final Exception ex) {
								Popup.alert(ex);
								ex.printStackTrace();
							}

						});

						// If the equipment is a default equipment, it's impossible to remove/modify it
						if (EquipmentEnum.isIn(equipment.getName())) {
							modifyButton.setVisible(false);
							deleteButton.setVisible(false);
						}

						mainVBox.getChildren().add(equipmentPane);
						mainVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));

					} catch (final Exception e) {
						Popup.alert(e);
						e.printStackTrace();
					}
				}

				mainVBox.setFillWidth(true);
				return mainVBox;
			}
		});
	}

	// To build and fill the person GridPane
	private final void buildPersonGridPane(final Person person, final GridPane personPane) {
		// Image on the left
		final InputStream is = MainController.class.getClassLoader()
				.getResourceAsStream(person.getGender().equals(Gender.FEMALE) ? "img/female.jpg" : "img/male.jpg");

		((ImageView) personPane.lookup("#personImage")).setImage(new Image(is));

		((Label) personPane.lookup("#firstNameLabel")).setText("First Name: " + person.getFirstName());
		((Label) personPane.lookup("#lastNameLabel")).setText("Last Name: " + person.getLastName());
		((Label) personPane.lookup("#personIDLabel")).setText("ID: " + person.getPersonID());
		((Label) personPane.lookup("#weightLabel")).setText("Weight: " + person.getWeight() + "kg");
		((Label) personPane.lookup("#heightLabel")).setText("Height: " + person.getHeight() + "cm");
		((Label) personPane.lookup("#genderLabel")).setText("Gender: " + person.getGender());
		((Label) personPane.lookup("#levelLabel")).setText("Level: " + person.getLevel());
		((Label) personPane.lookup("#birthdateLabel"))
				.setText("Birthdate: " + TimeHelper.getJustDateString(person.getBirthDate().getTime()));
	}

	// To build and fill the equipment GridPane
	private final void buildEquipmentGridPane(final Equipment equipment, final GridPane equipmentPane) {
		// Image on the left
		final InputStream is = MainController.class.getClassLoader()
				.getResourceAsStream("img/" + equipment.getFileName());

		((ImageView) equipmentPane.lookup("#equipmentImage")).setImage(new Image(is));
		((Label) equipmentPane.lookup("#nameLabel")).setText("Name: " + equipment.getName());
		((Label) equipmentPane.lookup("#idLabel")).setText("ID: " + equipment.getID());
	}

	// To build and fill the booking BorderPane
	private final void buildBookingBorderPane(final Booking booking, final BorderPane bookingBorderPane)
			throws IOException, SQLException {

		final GridPane mainPane = (GridPane) bookingBorderPane.lookup("#mainGridPane");
		final VBox bookingVBox = (VBox) bookingBorderPane.lookup("#bookingVBox");
		final HBox buttons = (HBox) bookingBorderPane.lookup("#buttons");

		// MainPane fields to fill
		((Label) mainPane.lookup("#equipmentNumberLabel")).setText("Equipment Number: " + booking.getEquipmentNumber());
		((Label) mainPane.lookup("#personNumberLabel")).setText("Person Number: " + booking.getPersonNumber());
		((Label) mainPane.lookup("#bookingIDLabel")).setText("BookingID: " + booking.getBookingID());
		((Label) mainPane.lookup("#startDateLabel"))
				.setText("Start Date: " + TimeHelper.getJustDateString(booking.getStartDate().getTime()));
		((Label) mainPane.lookup("#endDateLabel"))
				.setText("End Date: " + TimeHelper.getJustDateString(booking.getEndDate().getTime()));

		// Add new GridPane per PersonBooking in the MainPane and fill it
		for (final PersonBooking personBooking : booking.getPersonBookingList()) {

			final FXMLLoader fxmlLoader = new FXMLLoader(
					MainController.class.getClassLoader().getResource("fxml/PersonBooking.fxml"));

			final GridPane personBookingGridPane = this.buildPersonBookingGridPane(personBooking, //
					booking, //
					bookingBorderPane, //
					fxmlLoader.load());

			bookingVBox.getChildren().add(personBookingGridPane);

		}

		// To add new PersonBooking in the current booking
		final Button addButton = ((Button) buttons.lookup("#addPersonBookingJFXButton"));
		addButton.setOnAction(event -> {

			try {

				// Open new AddModifyPersonBookingWindow
				final AddModifyPersonBookingWindow stage = new AddModifyPersonBookingWindow(this.database, //
						this.personList, //
						this.equipmentList, //
						booking);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();

				if (stage.isValidated()) { // Just update field

					this.manageAddedPersonBooking(stage.getBooking(), //
							stage.getPersonBooking().getPerson(), //
							stage.getPersonBooking().getEquipment(), //
							stage.getPersonBooking().getStartDate(), //
							stage.getPersonBooking().getEndDate());
					MainController.this.updateBookingPagination();
				}

			} catch (final Exception ex) {
				Popup.alert(ex);
				ex.printStackTrace();
			}

		});

		// To delete booking and so all PersonBooking inside it
		final Button deleteButton = ((Button) buttons.lookup("#deleteBookingJFXButton"));
		deleteButton.setOnAction(event -> {

			try {

				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText("Deletion of the Booking " + booking);
				alert.setContentText("Are you sure to delete it?");

				// Ask confirmation
				final Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {

					this.database.deleteBooking(booking);
					this.updateBookingPagination();
				}

			} catch (final Exception ex) {
				Popup.alert(ex);
				ex.printStackTrace();
			}

		});
	}

	private final GridPane buildPersonBookingGridPane(final PersonBooking personBooking, //
			final Booking booking, //
			final BorderPane bookingBorderPane, //
			final GridPane personBookingGridPane) throws IOException {

		final Equipment equipment = personBooking.getEquipment();
		final Person person = personBooking.getPerson();

		// Image
		final InputStream is = MainController.class.getClassLoader()
				.getResourceAsStream("img/" + equipment.getFileName());

		((ImageView) personBookingGridPane.lookup("#equipmentImage")).setImage(new Image(is));
		((Label) personBookingGridPane.lookup("#equipmentNameLabel")).setText("Equipment Name: " + equipment.getName());
		((Label) personBookingGridPane.lookup("#equipmentIDLabel")).setText("EquipmentID: " + equipment.getID());
		((Label) personBookingGridPane.lookup("#firstNameLabel")).setText("First Name: " + person.getFirstName());
		((Label) personBookingGridPane.lookup("#lastNameLabel")).setText("Last Name: " + person.getLastName());
		((Label) personBookingGridPane.lookup("#personIDLabel")).setText("PersonID: " + person.getPersonID());
		((Label) personBookingGridPane.lookup("#weightLabel")).setText("Weight: " + person.getWeight() + "kg");
		((Label) personBookingGridPane.lookup("#heightLabel")).setText("Height: " + person.getHeight() + "cm");
		((Label) personBookingGridPane.lookup("#birthdateLabel"))
				.setText("Birthdate: " + TimeHelper.getJustDateString(person.getBirthDate().getTime()));
		((Label) personBookingGridPane.lookup("#levelLabel")).setText("Level: " + person.getLevel());
		((Label) personBookingGridPane.lookup("#genderLabel")).setText("Gender: " + person.getGender());

		// To modify the PersonBooking inside the booking
		final Button modifyButton = ((Button) personBookingGridPane.lookup("#modifyPersonBookingJFXButton"));
		modifyButton.setOnAction(event -> {

			try {

				// Open new AddModifyPersonBookingWindow with the fields filled
				final AddModifyPersonBookingWindow stage = new AddModifyPersonBookingWindow(this.database, //
						this.personList, //
						this.equipmentList, //
						booking, //
						personBooking);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();

				if (stage.isValidated()) { // Just update field

					this.manageAddedPersonBooking(stage.getBooking(), //
							stage.getPersonBooking().getPerson(), //
							stage.getPersonBooking().getEquipment(), //
							stage.getPersonBooking().getStartDate(), //
							stage.getPersonBooking().getEndDate());
					MainController.this.updateBookingPagination();
				}

			} catch (final Exception ex) {
				Popup.alert(ex);
				ex.printStackTrace();
			}

		});

		// To delete the PersonBooking
		final Button deleteButton = ((Button) personBookingGridPane.lookup("#deletePersonBookingJFXButton"));
		deleteButton.setOnAction(event -> {

			try {

				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText("Deletion of the PersonBooking " + personBooking);
				alert.setContentText("Are you sure to delete it?");

				// Ask confirmation
				final Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {

					this.database.deletePersonBooking(personBooking, booking.getBookingID());

					this.updateBookingPagination();
				}

			} catch (final Exception ex) {
				Popup.alert(ex);
				ex.printStackTrace();
			}

		});

		return personBookingGridPane;
	}

	private final void manageAddedPersonBooking(final Booking booking, //
			final Person person, //
			final Equipment equipment, //
			final Date startDate, //
			final Date endDate) throws SQLException, IOException {

		final Date dateRef = new Date(System.currentTimeMillis() - (TimeHelper.MS_PER_DAY * 365L * 10L));
		final String equipmentName = equipment.getName().toUpperCase();

		if (!equipmentName.contains(Equipment.EquipmentEnum.CASQUE.toString().toUpperCase())
				&& person.getBirthDate().after(dateRef)) {

			final PersonBooking personBookingCasque = new PersonBooking(person, //
					this.equipmentList.addEquipment(new Equipment(Equipment.EquipmentEnum.CASQUE.toString())), //
					startDate.getTime(), //
					endDate.getTime());
			booking.addPersonBooking(personBookingCasque);

			this.database.insertOrUpdateBooking(booking);
		}

		if (equipment.compareTo(new Equipment(Equipment.EquipmentEnum.SKi.toString())) == 0) {

			final PersonBooking personBookingBatons = new PersonBooking(person, //
					this.equipmentList.addEquipment(new Equipment(Equipment.EquipmentEnum.BATONS.toString())), //
					startDate.getTime(), //
					endDate.getTime());
			booking.addPersonBooking(personBookingBatons);

			this.database.insertOrUpdateBooking(booking);
		}
	}

	// Action Events
	@FXML
	public final void addNewBooking(final ActionEvent e) {
		try {
			final AddModifyPersonBookingWindow stage = new AddModifyPersonBookingWindow(this.database, //
					this.personList, //
					this.equipmentList);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			if (stage.isValidated()) { // Just update field
				this.manageAddedPersonBooking(stage.getBooking(), //
						stage.getPersonBooking().getPerson(), //
						stage.getPersonBooking().getEquipment(), //
						stage.getPersonBooking().getStartDate(), //
						stage.getPersonBooking().getEndDate());

				MainController.this.updateBookingPagination();
			}
		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	@FXML
	public final void addNewPerson(final ActionEvent e) {
		try {
			final AddModifyPersonWindow stage = new AddModifyPersonWindow(this.database);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			if (stage.isValidated()) {
				this.updatePersonPagination();
			}
		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	@FXML
	public final void addNewEquipment(final ActionEvent e) {
		try {
			final AddModifyEquipmentWindow stage = new AddModifyEquipmentWindow(this.database);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			if (stage.isValidated()) {
				this.updateEquipmentPagination();
			}
		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	// Import Json
	@FXML
	public final void loadBookingJsonFile(final ActionEvent e) {

		try {
			final FileChooser fileChooser = new FileChooser();
			final File selectedFile = fileChooser
					.showOpenDialog(((Stage) ((Node) e.getSource()).getScene().getWindow()));

			if (selectedFile != null) {
				final Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
				final TreeSet<Booking> out = new TreeSet<Booking>();
				out.addAll(Arrays.asList(gson.fromJson(new JsonReader(new FileReader(selectedFile)), Booking[].class)));

				for (final Booking booking : out) {
					try {

						for (final PersonBooking personBooking : booking.getPersonBookingList()) {

							if (StringUtils.isBlank(personBooking.getPerson().getFirstName())) {
								throw new Exception("First Name cannot be empty !");
							}

							if (StringUtils.isBlank(personBooking.getPerson().getLastName())) {
								throw new Exception("Last Name cannot be null !");
							}

							if (personBooking.getPerson().getGender() == null) {
								throw new Exception("Gender cannot bu null !");
							}

							if (personBooking.getPerson().getWeight() == 0) {
								throw new Exception("Weight cannot be null !");
							}

							if (personBooking.getPerson().getHeight() == 0) {
								throw new Exception("Height cannot be null !");
							}

							if (personBooking.getPerson().getLevel() == null) {
								throw new Exception("Level cannot be null !");
							}

							if (personBooking.getPerson().getBirthDate() == null) {
								throw new Exception("Birth Date cannot be empty !");
							}

							if (personBooking.getPerson().getBirthDate().after(new Date())) {
								throw new Exception("Birth Date must be before current date !");
							}

							if (StringUtils.isBlank(personBooking.getEquipment().getName())) {
								throw new Exception("Equipment Name cannot be empty !");
							}

							if (personBooking.getStartDate() == null) {
								throw new Exception("Start Date cannot be empty !");
							}

							if (personBooking.getEndDate() == null) {
								throw new Exception("End Date cannot be empty!");
							}

							if (!personBooking.getStartDate().before(personBooking.getEndDate())) {
								throw new Exception("Start Date must be before the End Date !");
							}

							this.database.insertOrUpdateBooking(booking);
						}

					} catch (final Exception ex) {
						Popup.alert(ex);
						ex.printStackTrace();
					}

				}
				this.database.deleteEmptyBooking();
				this.updateBookingPagination();
				this.updatePersonPagination();
				this.updateEquipmentPagination();
			}
		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	@FXML
	public final void loadPersonsJsonFile(final ActionEvent e) {

		try {
			final FileChooser fileChooser = new FileChooser();
			final File selectedFile = fileChooser
					.showOpenDialog(((Stage) ((Node) e.getSource()).getScene().getWindow()));

			if (selectedFile != null) {

				try (final JsonReader jr = new JsonReader(new FileReader(selectedFile))) {

					final Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
					final TreeSet<Person> out = new TreeSet<Person>();
					out.addAll(Arrays.asList(gson.fromJson(jr, Person[].class)));

					for (final Person person : out) {

						try {

							if (StringUtils.isBlank(person.getFirstName())) {
								throw new Exception("First Name cannot be empty !");
							}

							if (StringUtils.isBlank(person.getLastName())) {
								throw new Exception("Last Name cannot be null !");
							}

							if (person.getGender() == null) {
								throw new Exception("Gender cannot bu null !");
							}

							if (person.getWeight() == 0) {
								throw new Exception("Weight cannot be null !");
							}

							if (person.getHeight() == 0) {
								throw new Exception("Height cannot be null !");
							}

							if (person.getLevel() == null) {
								throw new Exception("Level cannot be null !");
							}

							if (person.getBirthDate() == null) {
								throw new Exception("Birth Date cannot be empty !");
							}

							if (person.getBirthDate().after(new Date())) {
								throw new Exception("Birth Date must be before current date !");
							}

							this.database.insertOrUpdatePerson(person);

						} catch (final Exception ex) {
							Popup.alert(ex);
							ex.printStackTrace();
						}
					}
				}
				this.updatePersonPagination();
			}
		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	@FXML
	public final void loadEquipmentsJsonFile(final ActionEvent e) {

		try {
			final FileChooser fileChooser = new FileChooser();
			final File selectedFile = fileChooser
					.showOpenDialog(((Stage) ((Node) e.getSource()).getScene().getWindow()));

			if (selectedFile != null) {

				try (final JsonReader jr = new JsonReader(new FileReader(selectedFile))) {

					final Gson gson = new Gson();
					final TreeSet<Equipment> out = new TreeSet<Equipment>();
					out.addAll(Arrays.asList(gson.fromJson(jr, Equipment[].class)));

					for (final Equipment equipment : out) {

						try {

							if (StringUtils.isBlank(equipment.getName())) {
								throw new Exception("Equipment Name cannot be empty !");
							}

							this.database.insertOrUpdateEquipment(equipment);

						} catch (final Exception ex) {
							Popup.alert(ex);
							ex.printStackTrace();
						}
					}
					this.updateEquipmentPagination();
				}
			}
		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	// Export Json
	@FXML
	public final void exportBookingJsonFile(final ActionEvent e) {

		try {
			final FileChooser fileChooser = new FileChooser();
			final File selectedFile = fileChooser
					.showSaveDialog(((Stage) ((Node) e.getSource()).getScene().getWindow()));

			if (selectedFile != null) {
				final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy").create();
				final FileWriter writer = new FileWriter(selectedFile);
				writer.write(gson.toJson(this.database.getAllBookings().toArray()));
				writer.close();

			}

		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	public final void exportPersonsJsonFile(final ActionEvent e) {

		try {
			final FileChooser fileChooser = new FileChooser();
			final File selectedFile = fileChooser
					.showSaveDialog(((Stage) ((Node) e.getSource()).getScene().getWindow()));

			if (selectedFile != null) {
				final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy").create();
				final FileWriter writer = new FileWriter(selectedFile);
				writer.write(gson.toJson(this.personList.getCopyList().toArray()));
				writer.close();

			}

		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}

	public final void exportEquipmentsJsonFile(final ActionEvent e) {

		try {
			final FileChooser fileChooser = new FileChooser();
			final File selectedFile = fileChooser
					.showSaveDialog(((Stage) ((Node) e.getSource()).getScene().getWindow()));

			if (selectedFile != null) {
				final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy").create();
				final FileWriter writer = new FileWriter(selectedFile);
				writer.write(gson.toJson(this.equipmentList.getCopyList().toArray()));
				writer.close();

			}

		} catch (final Exception ex) {
			Popup.alert(ex);
			ex.printStackTrace();
		}
	}
}
