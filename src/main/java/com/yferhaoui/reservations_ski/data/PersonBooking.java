package com.yferhaoui.reservations_ski.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yferhaoui.reservations_ski.data.Person;

import com.google.gson.annotations.Expose;

public final class PersonBooking implements Comparable<PersonBooking> {

	@Expose(serialize = true, deserialize = true)
	private Person person;

	@Expose(serialize = true, deserialize = true)
	private Equipment equipment;

	@Expose(serialize = true, deserialize = true)
	private Date start_date;

	@Expose(serialize = true, deserialize = true)
	private Date end_date;

	// Basic Constructor
	public PersonBooking(final Person person, //
			final Equipment equipment, //
			final long start_date, //
			final long end_date) {

		this.person = person;
		this.equipment = equipment;
		this.start_date = new Date(start_date);
		this.end_date = new Date(end_date);
	}

	// Database Constructor
	public PersonBooking(final Person person, final Equipment equipment, final ResultSet rs) throws SQLException {
		this.person = person;
		this.equipment = equipment;
		this.start_date = new Date(rs.getLong("start_date"));
		this.end_date = new Date(rs.getLong("end_date"));
	}

	// Getter
	public final Person getPerson() {
		return this.person;
	}

	public final Equipment getEquipment() {
		return this.equipment;
	}

	public final Date getStartDate() {
		return this.start_date;
	}

	public final Date getEndDate() {
		return this.end_date;
	}

	// Setter
	public final void setPerson(final Person person) {
		this.person = person;
	}

	public final void setEquipment(final Equipment equipment) {
		this.equipment = equipment;
	}

	public final void setStartDate(final long start_date) {
		this.start_date = new Date(start_date);
	}

	public final void setEndDate(final long end_date) {
		this.end_date = new Date(end_date);
	}

	@Override
	public final int compareTo(final PersonBooking arg0) {
		if (this.person.getPersonID().equals(arg0.person.getPersonID())) {
			return this.equipment.getName().compareTo(arg0.equipment.getName());
		}
		return this.getPerson().getPersonID().compareTo(arg0.person.getPersonID());

	}

	@Override
	public final String toString() {
		return this.person.toString() + " " + this.equipment;
	}
}
