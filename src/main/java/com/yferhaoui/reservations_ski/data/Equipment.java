package com.yferhaoui.reservations_ski.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.google.gson.annotations.Expose;
import com.yferhaoui.basic.helper.TimeHelper;

public final class Equipment implements Comparable<Equipment> {

	public enum EquipmentEnum {
		SKi("Ski", "ski.png"), //
		SURF("Snowboard", "snow.png"), //
		MONOSKI("Monoski", "monoski.png"), //
		PATINETTE("Patinette", "patinette.png"), //
		LUGE("Luge", "luge.png"), //
		CHAUSSURES_SKI("Chaussures Ski", "chaussure_ski.png"),
		CHAUSSURES_MONO("Chaussures Monoski", "chaussure_monoski.png"),
		CHAUSSURES_Surf("Chausures Snowboard", "chaussure_snow.png"), //
		CASQUE("Casque", "casque.png"), //
		BATONS("Batons de Ski", "batons.png");

		private final String name;
		private final String fileName;

		private EquipmentEnum(final String name, final String fileName) {
			this.name = name;
			this.fileName = fileName;
		}

		@Override
		public final String toString() {
			return this.name;
		}
		
		public final static String getFileName(final String name) {
			for (final EquipmentEnum e : EquipmentEnum.values()) {
				if (e.toString().equalsIgnoreCase(name)) {
					return e.fileName;
				}
			}
			return "no-image.png";
		}

		public final static boolean isIn(final String name) {
			return Arrays.stream(EquipmentEnum.values()).anyMatch(o -> o.toString().equalsIgnoreCase(name));
		}
	}
	
	@Expose(serialize = true, deserialize = true)
	private String equipment_id;

	@Expose(serialize = true, deserialize = true)
	private String equipment_name;

	@Expose(serialize = false, deserialize = false)
	private String fileName;

	// Basic Constructor
	public Equipment() {
		this.equipment_id = TimeHelper.getValidKeyHexa();
	}
	
	public Equipment(final String equipment_name) {
		this();
		this.equipment_name = equipment_name;
		this.fileName = EquipmentEnum.getFileName(this.equipment_name);
	}

	// Database Constructor
	public Equipment(final ResultSet rs) throws SQLException {
		this.equipment_id = rs.getString("equipment_ID");
		this.equipment_name = rs.getString("equipment_name");
		this.fileName = EquipmentEnum.getFileName(this.equipment_name);
	}

	// Getter
	public final String getID() {
		return this.equipment_id;
	}
	
	public final String getName() {
		return this.equipment_name;
	}

	public final String getFileName() {
		return this.fileName;
	}

	// Setter
	public final void setID(final String equipment_id) {
		this.equipment_id = equipment_id;
	}
	
	public final void setName(final String equipment_name) {
		this.equipment_name = equipment_name;
	}

	@Override
	public final int compareTo(final Equipment arg0) {
		return this.equipment_name.compareTo(arg0.equipment_name);
	}

	@Override
	public final String toString() {
		return this.equipment_name;
	}
}
