package com.yferhaoui.reservations_ski;

import java.io.IOException;
import java.sql.SQLException;

import com.yferhaoui.reservations_ski.controller.MainController;
import com.yferhaoui.reservations_ski.data.EquipmentList;
import com.yferhaoui.reservations_ski.data.PersonList;
import com.yferhaoui.reservations_ski.data.base.Database;
import com.yferhaoui.reservations_ski.scene.MainScene;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public final class ReservationSki extends Application {

	@Override
	public final void start(final Stage primaryStage) throws IOException, SQLException {

		// Init global datas
		final EquipmentList equipmentList = new EquipmentList();
		final PersonList personList = new PersonList();
		final Database database = new Database(Database.DataBase.RESERVATION_SKI, personList, equipmentList);

		final FXMLLoader loader = new FXMLLoader(ReservationSki.class.getClassLoader().getResource("fxml/Main.fxml"));
		
		// Set programmatically the controler
		loader.setController(new MainController(database, personList, equipmentList)); 

		primaryStage.setTitle("Inventory Manager");
		primaryStage.setScene(new MainScene(loader));
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public final void handle(final WindowEvent we) {
				System.exit(0);
			}
		});
		primaryStage.show();

	}
}
