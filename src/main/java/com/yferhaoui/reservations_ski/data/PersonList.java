package com.yferhaoui.reservations_ski.data;

import java.util.TreeSet;

public final class PersonList {

	private final TreeSet<Person> persons = new TreeSet<Person>();

	// Add person and return it
	public final Person addPerson(final Person person) {
		if (this.persons.contains(person)) {

			return this.persons.stream()//
					.filter(o -> o.equals(person))//
					.findFirst()//
					.orElse(person);
		}
		this.persons.add(person);
		return person;
	}
	
	public final boolean remove(final Person person) {
		return this.persons.remove(person);
	}

	// Getter
	public final TreeSet<Person> getCopyList() {
		return new TreeSet<Person>(this.persons);
	}
}
