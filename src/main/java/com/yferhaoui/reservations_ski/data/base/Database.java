package com.yferhaoui.reservations_ski.data.base;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import com.yferhaoui.basic.helper.FileHelper;
import com.yferhaoui.reservations_ski.data.Booking;
import com.yferhaoui.reservations_ski.data.Equipment;
import com.yferhaoui.reservations_ski.data.Equipment.EquipmentEnum;
import com.yferhaoui.reservations_ski.data.EquipmentList;
import com.yferhaoui.reservations_ski.data.Person;
import com.yferhaoui.reservations_ski.data.PersonBooking;
import com.yferhaoui.reservations_ski.data.PersonList;
import com.yferhaoui.reservations_ski.util.Constants;

public final class Database {

	public enum DataBase {
		TEST, RESERVATION_SKI
	}

	private enum Table {
		EQUIPMENT, PERSON, BOOKING, PERSON_BOOKING;
	}

	private final DataBase database;
	private final boolean autoCommit;
	private final PersonList personList;
	private final EquipmentList equipmentList;

	public Database(final DataBase database, final PersonList personList, final EquipmentList equipmentList)
			throws SQLException, UnsupportedEncodingException {

		this.database = database;
		// If mode Test don't commit
		this.autoCommit = !this.database.equals(DataBase.TEST);
//		this.autoCommit = true;
		this.personList = personList;
		this.equipmentList = equipmentList;

		if (!this.autoCommit) {
			Constants.dropDatabase(this.database);
		}

		// Tables creation
		try (final Connection connection = this.getConnection()) {
			final Statement statement = connection.createStatement();

			statement.executeUpdate("CREATE TABLE IF NOT EXISTS EQUIPMENT "
					+ " (equipment_id				TEXT			PRIMARY KEY							NOT NULL, "
					+ " equipment_name 				TEXT 												NOT NULL)");

			statement.executeUpdate("CREATE TABLE IF NOT EXISTS PERSON "
					+ " (person_id					TEXT	      	PRIMARY KEY			                NOT NULL, "
					+ " first_name          		TEXT                                                NOT NULL, "
					+ " last_name           		TEXT                                                NOT NULL, "
					+ " gender              		TEXT	                                            NOT NULL, "
					+ " weight              		SMALLINT                                            NOT NULL, "
					+ " height              		SMALLINT                                            NOT NULL, "
					+ " level               		TEXT                             					NOT NULL, "
					+ " birth_date					DATE												NOT NULL)");

			statement.executeUpdate("CREATE TABLE IF NOT EXISTS BOOKING "
					+ " (booking_id					TEXT	    PRIMARY KEY			        			NOT NULL)");

			statement.executeUpdate("CREATE TABLE IF NOT EXISTS PERSON_BOOKING "
					+ " (equipment_id					TEXT											NOT NULL, "
					+ " person_id	          			TEXT                                            NOT NULL, "
					+ " booking_id           			TEXT                                            NOT NULL, "
					+ " start_date            			BIGINT	                                        NOT NULL, "
					+ " end_date              			BIGINT                                          NOT NULL, "
					+ " CONSTRAINT fk_equipment_id 		FOREIGN KEY (equipment_id) 			REFERENCES EQUIPMENT(equipment_id) 		ON DELETE CASCADE ON UPDATE CASCADE, "
					+ " CONSTRAINT fk_person_id     	FOREIGN KEY (person_id) 			REFERENCES PERSON(person_id)      		ON DELETE CASCADE ON UPDATE CASCADE, "
					+ " CONSTRAINT fk_booking_id     	FOREIGN KEY (booking_id) 			REFERENCES BOOKING(booking_id) 			ON DELETE CASCADE ON UPDATE CASCADE, "
					+ " CONSTRAINT pk_person_booking 	PRIMARY KEY (equipment_id, person_id, booking_id))");

		}

		// Init equipments if doesn't exists
		for (final EquipmentEnum equipmentEnum : Equipment.EquipmentEnum.values()) {

			this.insertOrUpdateEquipment(new Equipment(equipmentEnum.toString()));
		}

	}

	// Open Connection
	public final synchronized Connection getConnection() throws SQLException {

		final Connection connection = DriverManager//
				.getConnection("jdbc:sqlite:" + FileHelper.setPath(new File(Constants.pathDB + database + ".db")));
		// To enable Foreign keys in sqlite
		connection.createStatement().executeUpdate("PRAGMA foreign_keys = ON");
		return connection;
	}

	// SELECT
	public final synchronized TreeSet<Booking> getAllBookings() throws SQLException {

		this.deleteEmptyBooking();

		try (final Connection connection = this.getConnection()) {

			final Statement statement = connection.createStatement();
			final ResultSet rs1 = statement.executeQuery("SELECT DISTINCT(booking_id) FROM " + Table.BOOKING);

			final TreeSet<Booking> bookings = new TreeSet<Booking>();
			while (rs1.next()) { // Iterate all bookings

				// Select all rows Natural joigned between PersonBooking, Equipment and Person
				// of booking
				final String sql = new StringBuilder("SELECT * FROM ")//
						.append(Table.PERSON_BOOKING)//
						.append(" NATURAL JOIN ")//
						.append(Table.EQUIPMENT)//
						.append(" NATURAL JOIN ")//
						.append(Table.PERSON)//
						.append(" WHERE booking_id = ?")//
						.toString();
				final PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, rs1.getString("booking_id"));
				final ResultSet rs2 = ps.executeQuery();

				final TreeSet<PersonBooking> person_booking = new TreeSet<PersonBooking>();
				while (rs2.next()) { // Iterate all Person Booking of a Booking

					// Get Person's Booking
					final Person person = this.personList.addPerson(new Person(rs2));

					// Get Equipment's Booking
					final Equipment equipment = this.equipmentList.addEquipment(new Equipment(rs2));
					person_booking.add(new PersonBooking(person, equipment, rs2));
				}
				bookings.add(new Booking(rs1, person_booking));

			}

			return bookings;
		}
	}

	public final synchronized TreeSet<Person> getAllPersons() throws SQLException {

		try (final Connection connection = this.getConnection()) {

			final TreeSet<Person> persons = new TreeSet<Person>();
			final ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + Table.PERSON);

			while (rs.next()) { // Iterate all Persons

				// Get Person's Booking
				final Person person = this.personList.addPerson(new Person(rs));
				persons.add(person);
			}

			return persons;
		}
	}

	public final synchronized TreeSet<Equipment> getAllEquipments() throws SQLException {

		try (final Connection connection = this.getConnection()) {

			final TreeSet<Equipment> equipments = new TreeSet<Equipment>();
			final ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + Table.EQUIPMENT);

			while (rs.next()) { // Iterate all Equipment

				// Get Person's Booking
				final Equipment equipment = this.equipmentList.addEquipment(new Equipment(rs));
				equipments.add(equipment);
			}

			return equipments;
		}
	}

	// INSERT
	public final synchronized int insertOrUpdatePerson(Person person) throws SQLException {

		person = this.personList.addPerson(person);
		final String sql = new StringBuilder("INSERT INTO ")//
				.append(Table.PERSON)//
				.append("(person_id, first_name, last_name, gender, weight, height, level, birth_date) VALUES(?,?,?,?,?,?,?,?)")//
				.append(" ON CONFLICT(person_id) DO UPDATE SET first_name = ?, last_name = ?, gender = ?, weight = ?, height = ?, level = ?, birth_date = ?")
				.toString();

		try (final Connection connection = this.getConnection()) {

			final PreparedStatement ps = connection.prepareStatement(sql);

			// To Insert
			ps.setString(1, person.getPersonID());
			ps.setString(2, person.getFirstName());
			ps.setString(3, person.getLastName());
			ps.setString(4, person.getGender().toString());
			ps.setInt(5, person.getWeight());
			ps.setInt(6, person.getHeight());
			ps.setString(7, person.getLevel().toString());
			ps.setDate(8, new java.sql.Date(person.getBirthDate().getTime()));

			// Or to Update
			ps.setString(9, person.getFirstName());
			ps.setString(10, person.getLastName());
			ps.setString(11, person.getGender().toString());
			ps.setInt(12, person.getWeight());
			ps.setInt(13, person.getHeight());
			ps.setString(14, person.getLevel().toString());
			ps.setDate(15, new java.sql.Date(person.getBirthDate().getTime()));

			return ps.executeUpdate();
		}
	}

	public final synchronized int insertOrUpdateEquipment(Equipment equipment) throws SQLException {

		try (final Connection connection = this.getConnection()) {

			final PreparedStatement ps = connection
					.prepareStatement("SELECT * FROM " + Table.EQUIPMENT + " WHERE equipment_name = ? COLLATE NOCASE");
			ps.setString(1, equipment.getName());
			final ResultSet rs = ps.executeQuery();

			// To check the name isn't already used
			if (!rs.next()) {

				equipment = this.equipmentList.addEquipment(equipment);
				final String sql = new StringBuilder("INSERT INTO ")//
						.append(Table.EQUIPMENT)//
						.append("(equipment_id, equipment_name) VALUES(?,?) ON CONFLICT(equipment_id) DO UPDATE SET equipment_name = ?")//
						.toString();

				final PreparedStatement ps1 = connection.prepareStatement(sql);

				// To Insert
				ps1.setString(1, equipment.getID());
				ps1.setString(2, equipment.getName());

				// Or to Update
				ps1.setString(3, equipment.getName());

				return ps1.executeUpdate();
			}

			// If exists, just update his ID
			final Equipment equipment2 = new Equipment(rs);
			equipment.setID(equipment2.getID());
			return 0;
		}
	}

	public final synchronized int insertOrUpdateBooking(final Booking booking) throws SQLException {

		try (final Connection connection = this.getConnection()) {

			// Insert or Update Booking
			final PreparedStatement ps1 = connection.prepareStatement("INSERT INTO " + Table.BOOKING
					+ "(booking_id) VALUES(?) ON CONFLICT(booking_id) DO UPDATE SET booking_id = booking_id");
			ps1.setString(1, booking.getBookingID());

			int res = ps1.executeUpdate();
			// For all PersonBooking inside Booking Insert or Update it
			for (final PersonBooking personBooking : booking.getPersonBookingList()) {

				res += this.insertOrUpdatePerson(personBooking.getPerson());
				res += this.insertOrUpdateEquipment(personBooking.getEquipment());
				
				final String sql2 = new StringBuilder("INSERT INTO ")//
						.append(Table.PERSON_BOOKING)//
						.append("(equipment_id, person_id, booking_id, start_date, end_date) VALUES(?,?,?,?,?)")//
						.append(" ON CONFLICT(equipment_id, person_id, booking_id) DO UPDATE SET start_date = ?, end_date = ?")//
						.toString();
				final PreparedStatement ps3 = connection.prepareStatement(sql2);

				// To Insert
				ps3.setString(1, personBooking.getEquipment().getID());
				ps3.setString(2, personBooking.getPerson().getPersonID());
				ps3.setString(3, booking.getBookingID());
				ps3.setLong(4, personBooking.getStartDate().getTime());
				ps3.setLong(5, personBooking.getEndDate().getTime());
				
				// To Update
				ps3.setLong(6, personBooking.getStartDate().getTime());
				ps3.setLong(7, personBooking.getEndDate().getTime());

				res += ps3.executeUpdate();
			}

			return res;
		}
	}

	// DELETE
	public final synchronized int deleteEquipment(final Equipment equipment) throws SQLException {

		try (final Connection connection = this.getConnection()) {

			final PreparedStatement ps = connection
					.prepareStatement("DELETE FROM " + Table.EQUIPMENT + " WHERE equipment_id = ?");
			ps.setString(1, equipment.getID());

			return ps.executeUpdate();
		}
	}

	public final synchronized int deletePerson(final Person person) throws SQLException {

		try (final Connection connection = this.getConnection()) {

			final PreparedStatement ps = connection
					.prepareStatement("DELETE FROM " + Table.PERSON + " WHERE person_id = ?");
			ps.setString(1, person.getPersonID());

			return ps.executeUpdate();
		}
	}

	public final synchronized int deletePersonBooking(final PersonBooking personBooking, final String booking_id)
			throws SQLException {

		int res = 0;
		try (final Connection connection = this.getConnection()) {

			final PreparedStatement ps = connection.prepareStatement("DELETE FROM " + Table.PERSON_BOOKING
					+ " WHERE booking_id = ? and equipment_id = ? and person_id = ?");
			ps.setString(1, booking_id);
			ps.setString(2, personBooking.getEquipment().getID().toString());
			ps.setString(3, personBooking.getPerson().getPersonID());

			res += ps.executeUpdate();
		}
		return res + this.deleteEmptyBooking();
	}

	public final synchronized int deleteBooking(final Booking booking) throws SQLException {

		try (final Connection connection = this.getConnection()) {

			final PreparedStatement ps = connection
					.prepareStatement("DELETE FROM " + Table.BOOKING + " WHERE booking_id = ?");
			ps.setString(1, booking.getBookingID());

			return ps.executeUpdate();
		}
	}

	// Delete Booking when no PERSON_BOOKING reference it
	public final synchronized int deleteEmptyBooking() throws SQLException {

		try (final Connection connection = this.getConnection()) {

			return connection.createStatement().executeUpdate("DELETE FROM " + Table.BOOKING
					+ " WHERE booking_id NOT IN (SELECT booking_id FROM " + Table.PERSON_BOOKING + ")");
		}
	}
}
