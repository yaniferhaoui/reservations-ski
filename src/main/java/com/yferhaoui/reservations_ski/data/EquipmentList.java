package com.yferhaoui.reservations_ski.data;

import java.util.TreeSet;

public final class EquipmentList {

	private final TreeSet<Equipment> equipments = new TreeSet<Equipment>();

	// Add equipment and return it
	public final Equipment addEquipment(final Equipment equipment) {
		if (this.equipments.contains(equipment)) {
			return this.equipments.stream()//
					.filter(o -> o.equals(equipment))//
					.findFirst()//
					.orElse(equipment);
		}

		final Equipment equipment2 = this.equipments.stream()//
				.filter(o -> o.getName().equalsIgnoreCase(equipment.getName()))//
				.findFirst()//
				.orElse(null);
		if (equipment2 != null) {
			return equipment2;
		}

		this.equipments.add(equipment);
		return equipment;
	}
	
	public final boolean remove(final Equipment equipment) {
		return this.equipments.remove(equipment);
	}

	// Getter
	public final TreeSet<Equipment> getCopyList() {
		return new TreeSet<Equipment>(this.equipments);
	}
}
