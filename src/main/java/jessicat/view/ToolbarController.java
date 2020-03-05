package jessicat.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import jessicat.model.CONSTRAINTType;
import jessicat.model.JythonObjectFactory;
import jessicat.modelChecker.MainApp;

public class ToolbarController {

	private MainApp mainApp;
	
	public ToolbarController() {
		
	}
	
	@FXML
	private void initialise() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void handleSave() throws IOException {
		System.err.println("handleSave()");
		boolean file_created = mainApp.getModels().save();
		if(file_created) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Project Saved");
			alert.setHeaderText("Your project has been saved!");
			alert.showAndWait();
		}
	}
	
	@FXML
	private void handleClose() {
		System.err.println("handleClose()");
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close the Current Project");
		alert.setHeaderText("Would you like to close the current project?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			mainApp.clearModels();
			mainApp.clearLayout();
			mainApp.showOpenScreen();
		}
	}
	
	@FXML
	private void handleNewConstraint() {
		System.err.println("handleNewConstraint()");
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Constraint Name");
		dialog.setHeaderText("Enter a Name for this Constraint");
		dialog.setContentText("Constraint Name:");
		CONSTRAINTType constraint = null;
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			//Get the constraint to be created
			String constraint_name = result.get();
			//Set up a constraint class to store information to be modified
			JythonObjectFactory factory = new JythonObjectFactory(CONSTRAINTType.class, "CONSTRAINTS", "Constraint");
			constraint = (CONSTRAINTType) factory.createObject(constraint_name);
			//Add the constraint to the list of constraints for this set of models
			mainApp.getModels().getConstraints().add(constraint);
			
			//Get the observations to display
			Set<List<String>> observations = mainApp.getModels().getZModel().getObservations();
			List<String> state_r = new ArrayList<String>();
			state_r.add("state");
			state_r.add(mainApp.getModels().getState());
			observations.add(state_r);
			
			//Show the observations for this constraint
			mainApp.showObservations(observations,constraint);
		}
		
	}
	
	@FXML
	private void handleRunChecker() {
		System.err.println("handleRunChecker()");
		mainApp.clearCenter();
		mainApp.clearRight();
		mainApp.showConsRunner();
	}
	
}
