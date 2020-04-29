package com.yferhaoui.reservations_ski.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import com.google.gson.annotations.Expose;
import com.yferhaoui.basic.helper.TimeHelper;

public final class Booking implements Comparable<Booking> {

	@Expose(serialize = false, deserialize = false)
	private final String booking_id;

	@Expose(serialize = true, deserialize = true)
	private TreeSet<PersonBooking> person_booking;

	// Gson Constructor
	private Booking() {
		this.booking_id = TimeHelper.getValidKeyHexa();
	}

	// Basic Constructor
	public Booking(final TreeSet<PersonBooking> person_booking) {
		this();
		this.person_booking = person_booking;
	}

	// Database Constructor
	public Booking(final ResultSet rs, final TreeSet<PersonBooking> person_booking) throws SQLException {
		this.booking_id = rs.getString("booking_id");
		this.person_booking = person_booking;
	}

	// Getter
	public final String getBookingID() {
		return this.booking_id;
	}
	
	public final TreeSet<PersonBooking> getPersonBookingList() {
		return this.person_booking;
	}

	public final Date getStartDate() {
		return this.person_booking.stream()//
				.min(Comparator.comparing(PersonBooking::getStartDate))//
				.orElseThrow(NoSuchElementException::new)//
				.getEndDate();
	}

	public final Date getEndDate() {
		return this.person_booking.stream()//
				.max(Comparator.comparing(PersonBooking::getEndDate))//
				.orElseThrow(NoSuchElementException::new)//
				.getEndDate();
	}
	
	public final int getEquipmentNumber() {
		return this.person_booking.size();
	}
	
	public final int getPersonNumber() {
		final TreeSet<Person> persons = new TreeSet<Person>();
		for (final PersonBooking personBooking : this.person_booking) {
			persons.add(personBooking.getPerson());
		}
		return persons.size();
	}

	// Setter
	public final boolean addPersonBooking(final PersonBooking person_booking) {
		return this.person_booking.add(person_booking);
	}

	public final boolean removePersonBooking(final PersonBooking person_booking) {
		return this.person_booking.remove(person_booking);
	}

	@Override
	public final int compareTo(final Booking arg0) {
		final Date startDate1 = this.getStartDate();
		final Date startDate2 = arg0.getStartDate();
		if (startDate1.equals(startDate2)) {
			return this.booking_id.compareTo(arg0.booking_id);
		}
		return startDate1.compareTo(startDate2);
	}
	
	@Override
	public final String toString() {
		return this.booking_id;
	}
}
