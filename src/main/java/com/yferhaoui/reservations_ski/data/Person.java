package com.yferhaoui.reservations_ski.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.yferhaoui.basic.helper.TimeHelper;

public final class Person implements Comparable<Person>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2335921100792183976L;

	@Expose(serialize = true, deserialize = true)
	private final String person_id;

	@Expose(serialize = true, deserialize = true)
	private String first_name;

	@Expose(serialize = true, deserialize = true)
	private String last_name;

	@Expose(serialize = true, deserialize = true)
	private Gender gender;

	@Expose(serialize = true, deserialize = true)
	private int weight;

	@Expose(serialize = true, deserialize = true)
	private int height;

	@Expose(serialize = true, deserialize = true)
	private Level level;

	@Expose(serialize = true, deserialize = true)
	private Date birth_date;

	// Gson Constructor
	public Person() {
		this.person_id = TimeHelper.getValidKeyHexa();
	}

	// Basic Constructor
	public Person(final String first_name, //
			final String last_name, //
			final Gender gender, //
			final int weight, //
			final int height, //
			final Level level, //
			final long birth_date) {

		this();
		this.first_name = first_name;
		this.last_name = last_name;
		this.gender = gender;
		this.weight = weight;
		this.height = height;
		this.level = level;
		this.birth_date = new Date(birth_date);
	}

	// Database Constructor
	public Person(final ResultSet rs) throws SQLException {
		this.person_id = rs.getString("person_id");
		this.first_name = rs.getString("first_name");
		this.last_name = rs.getString("last_name");
		this.gender = Gender.valueOf(rs.getString("gender"));
		this.weight = rs.getInt("weight");
		this.height = rs.getInt("height");
		this.level = Level.valueOf(rs.getString("level"));
		this.birth_date = rs.getDate("birth_date");
		
	}

	// Getter
	public final String getPersonID() {
		return this.person_id;
	}

	public final String getFirstName() {
		return this.first_name;
	}

	public final String getLastName() {
		return this.last_name;
	}

	public final Gender getGender() {
		return this.gender;
	}

	public final int getWeight() {
		return this.weight;
	}

	public final int getHeight() {
		return this.height;
	}

	public final Level getLevel() {
		return this.level;
	}
	
	public final Date getBirthDate() {
		return this.birth_date;
	}

	// Setter
	public final void setFirstName(final String first_name) {
		this.first_name = first_name;
	}

	public final void setLastName(final String last_name) {
		this.last_name = last_name;
	}

	public final void setGender(final Gender gender) {
		this.gender = gender;
	}

	public final void setWeight(final int weight) {
		this.weight = weight;
	}

	public final void setHeight(final int height) {
		this.height = height;
	}

	public final void setLevel(final Level level) {
		this.level = level;
	}
	
	public final void setBirthDate(final Date birth_date) {
		this.birth_date = birth_date;
	}

	@Override
	public final int compareTo(final Person arg0) {
		return this.person_id.compareTo(arg0.person_id);
	}

	@Override
	public final String toString() {
		return this.first_name + " " + this.last_name;
	}
}
