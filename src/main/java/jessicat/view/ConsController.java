package jessicat.view;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import jessicat.model.CONSTRAINTType;
import jessicat.modelChecker.MainApp;

public class ConsController {
	
	@FXML 
	ListView consList;
	
	private MainApp mainApp;
	
	public ConsController() {
		
	}
	
	@FXML
	private void initialise() {
		
	}
	
	/**
	 * Set the mainApp to reference for this controller (similar method for all controllers). 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		consList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mainApp.showConsView(consList.getSelectionModel().getSelectedItem().toString());
			}
		});
	}
	
	/**
	 * Display the constraints to the end-user. 
	 */
	public void display() {
		clearList();
		
		ArrayList<CONSTRAINTType> cons = mainApp.getModels().getConstraints();
		
		for(CONSTRAINTType c: cons) {
			String con_name = c.getName();
			consList.getItems().add(con_name);
		}
	}
	
	/**
	 * Clear the list of constraints. 
	 */
	public void clearList() {
		consList.getItems().clear();
	}

}
