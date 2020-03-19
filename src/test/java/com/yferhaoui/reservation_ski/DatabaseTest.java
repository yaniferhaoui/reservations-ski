package com.yferhaoui.reservation_ski;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.ibatis.javassist.NotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.yferhaoui.basic.helper.MathHelper;
import com.yferhaoui.basic.helper.TimeHelper;
import com.yferhaoui.reservations_ski.data.Booking;
import com.yferhaoui.reservations_ski.data.Equipment;
import com.yferhaoui.reservations_ski.data.Equipment.EquipmentEnum;
import com.yferhaoui.reservations_ski.data.EquipmentList;
import com.yferhaoui.reservations_ski.data.Gender;
import com.yferhaoui.reservations_ski.data.Level;
import com.yferhaoui.reservations_ski.data.Person;
import com.yferhaoui.reservations_ski.data.PersonBooking;
import com.yferhaoui.reservations_ski.data.PersonList;
import com.yferhaoui.reservations_ski.data.base.Database;
import com.yferhaoui.reservations_ski.util.Constants;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public final class DatabaseTest {

	private final Equipment equipment1 = new Equipment(EquipmentEnum.SKi.toString());
	private final Equipment equipment2 = new Equipment(EquipmentEnum.SURF.toString());
	private final Equipment equipment3 = new Equipment(EquipmentEnum.LUGE.toString());

	private final Person person1 = new Person("Jules", "Petit", Gender.MALE, 78, 181, Level.CONFIRME, //
			System.currentTimeMillis() - TimeHelper.msPerDay * 365 * 19 + TimeHelper.msPerDay * 42);
	
	private final Person person2 = new Person("Antoine", "Grand", Gender.MALE, 67, 175, Level.DEBUTANT, //
			System.currentTimeMillis() - TimeHelper.msPerDay * 365 * 9 + TimeHelper.msPerDay * 188);
	
	private final Person person3 = new Person("Lisa", "Mayet", Gender.FEMALE, 67, 168, Level.EXPERT, //
			System.currentTimeMillis() - TimeHelper.msPerDay * 365 * 15 + TimeHelper.msPerDay * 12);

	// Booking 1
	private final PersonBooking personBooking1A = new PersonBooking(this.person1, //
			this.equipment1, //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay), //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay) + TimeHelper.msPerDay);

	private final PersonBooking personBooking2A = new PersonBooking(this.person2, //
			this.equipment3, //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay), //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay) + TimeHelper.msPerDay);

	private final PersonBooking personBooking3A = new PersonBooking(this.person3, //
			this.equipment2, //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay), //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay) + TimeHelper.msPerDay);

	private final TreeSet<PersonBooking> personBookings1 = new TreeSet<PersonBooking>(//
			Arrays.asList(new PersonBooking[] { //
					personBooking1A, personBooking2A, personBooking3A//
			}));

	private final Booking booking1 = new Booking(personBookings1);

	// Booking 2
	private final PersonBooking personBooking1B = new PersonBooking(this.person1, //
			this.equipment3, //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay), //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay) + TimeHelper.msPerDay);

	private final PersonBooking personBooking2B = new PersonBooking(this.person2, //
			this.equipment2, //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay), //
			System.currentTimeMillis() + MathHelper.nextLong(TimeHelper.msPerDay) + TimeHelper.msPerDay);

	private final TreeSet<PersonBooking> personBookings2 = new TreeSet<PersonBooking>(//
			Arrays.asList(new PersonBooking[] { //
					personBooking1B, personBooking2B//
			}));

	private final Booking booking2 = new Booking(personBookings2);

	// ---------------- //
	private final Database database;
	private final PersonList personList = new PersonList();
	private final EquipmentList equipmentList = new EquipmentList();

	public DatabaseTest() throws SQLException, UnsupportedEncodingException {
		this.database = new Database(Database.DataBase.TEST, personList, equipmentList);
	}

	@Test
	@Order(1)
	public final void testSelectNullBooking() throws SQLException {
		final TreeSet<Booking> res = this.database.getAllBookings();

		Assertions.assertEquals(0, res.size()); // Must be empty
	}

	@Test
	@Order(2)
	public final void testInsertBooking() throws SQLException {
		final int res1 = this.database.insertOrUpdateBooking(this.booking1);

		Assertions.assertEquals(7, res1);

		final int res2 = this.database.insertOrUpdateBooking(this.booking2);

		Assertions.assertEquals(5, res2);
		
	}

	@Test
	@Order(3)
	public final void testSelectInsertedBooking() throws SQLException {
		final TreeSet<Booking> res = this.database.getAllBookings();

		Assertions.assertEquals(2, res.size());
	}

	@Test
	@Order(4)
	public final void testUpdateBooking() throws SQLException {
		this.personBooking1A.setStartDate(System.currentTimeMillis());
		this.person1.setFirstName("Jacques");

		final int res = this.database.insertOrUpdateBooking(this.booking1);

		Assertions.assertEquals(7, res);
	}

	@Test
	@Order(5)
	public final void testSelectUpdatedBooking() throws SQLException, NotFoundException {
		final TreeSet<Booking> res = this.database.getAllBookings();

		Assertions.assertEquals(2, res.size());

		for (final Booking booking : res) {
			for (final PersonBooking personBooking : booking.getPersonBookingList()) {
				if (personBooking.getStartDate().equals(this.personBooking1A.getStartDate())
						&& personBooking.getPerson().getFirstName().equals(this.person1.getFirstName())) {
					return; // Update found !
				}
			}
		}
		throw new NotFoundException("Update not found !");
	}

	@Test
	@Order(6)
	public final void testSelectAllPersons() throws SQLException {

		final TreeSet<Person> persons = this.database.getAllPersons();

		Assertions.assertEquals(3, persons.size());
	}

	@Test
	@Order(7)
	public final void testSelectAllEquipments() throws SQLException {

		final TreeSet<Equipment> equipments = this.database.getAllEquipments();

		Assertions.assertEquals(10, equipments.size());
	}

	@Test
	@Order(8)
	public final void testDeletePersonBooking() throws SQLException {

		final int res = this.database.deletePersonBooking(this.personBooking1A, this.booking1.getBookingID());

		Assertions.assertEquals(1, res);
	}

	@Test
	@Order(9)
	public final void testDeleteBooking() throws SQLException {

		final int res = this.database.deleteBooking(this.booking1);

		Assertions.assertEquals(1, res);
	}

	@Test
	@Order(10)
	public final void testDeletePerson() throws SQLException {

		final int res = this.database.deletePerson(this.person1);

		Assertions.assertEquals(1, res);

		final TreeSet<Person> persons = this.database.getAllPersons();

		Assertions.assertEquals(2, persons.size());
	}

	@AfterAll
	public final void closeAndDeleteDatabase() throws SQLException, UnsupportedEncodingException {
		this.database.getConnection().rollback();
		this.database.getConnection().close();
		Constants.dropDatabase(Database.DataBase.TEST);
	}
}