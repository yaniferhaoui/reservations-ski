package com.yferhaoui.reservations_ski.data;

public enum Gender {
	MALE("Homme"), FEMALE("Femme");

	private final String name;

	private Gender(final String name) {
		this.name = name;
	}
	
	public final String getName() {
		return this.name;
	}
}
