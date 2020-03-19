package com.yferhaoui.reservations_ski.util;

import java.io.File;

import com.yferhaoui.basic.helper.FileHelper;
import com.yferhaoui.reservations_ski.data.base.Database;

public final class Constants {

	public final static String pathDB = new StringBuilder(".cache")//
			.append(File.separator)//
			.toString();

	// Return true if successfull deletion
	public final static boolean dropDatabase(final Database.DataBase database) {
		final File file = FileHelper.setPath(new File(Constants.pathDB + database + ".db"));
		return file.delete();
	}
}
