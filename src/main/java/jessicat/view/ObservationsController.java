package jessicat.view;



import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import jessicat.model.CONSTRAINTType;
import jessicat.modelChecker.MainApp;

public class ObservationsController {
	
	private MainApp mainApp;
	private String type;
	private static String STARTING_OBSERVATIONS = "start";
	private static String ENDING_OBSERVATIONS = "end";
	
	@FXML 
	public GridPane myGridPane;
	
	@FXML
	public Label title;
	
	public ObservationsController() {
	
	}
	
	@FXML
	private void initialise() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setType(String type) {
		this.type = type;
		if(type.equals(STARTING_OBSERVATIONS)) {
			title.setText("Starting Observations");
		} else if(type.equals(ENDING_OBSERVATIONS)) {
			title.setText("Ending Observations");
		}
	}
	
	/**
	 * Store the new observations for this constraint when the end-user hits the "OK" button. 
	 */
	@FXML
	private void handleOk() {
		CONSTRAINTType constraint = mainApp.getModels().getNewestConstraint();
		ObservableList<Node> children = myGridPane.getChildren();
		for(int i = 0; i < children.size(); i+=2) {
			String observation_name = ((Label) children.get(i)).getText().replaceAll(":", "");
			String observation_value = ((TextField) children.get(i+1)).getText();
			if(type.equals(STARTING_OBSERVATIONS)) {
				constraint.addObservation(observation_name,  observation_value, true);
			} else {
				constraint.addObservation(observation_name, observation_value, false);
			}
		}
		if(type.equals(STARTING_OBSERVATIONS)) {
			switchTypes();
		} else {
			mainApp.clearCenter();
			mainApp.showSeqInput();
		}
	}
	
	/**
	 * Switch between entering starting observations or ending observations, ensure the right values are displayed. 
	 */
	private void switchTypes() {
		if(type.equals(STARTING_OBSERVATIONS)) {
			this.setType(ENDING_OBSERVATIONS);
			CONSTRAINTType constraint = mainApp.getModels().getNewestConstraint();
			if(constraint.getEndObservations().size() > 0) {
				ObservableList<Node> children = myGridPane.getChildren();
				for(int i = 0; i < children.size(); i+=2) {
					String o_name = ((Label) children.get(i)).getText().replaceAll(":", "");
					TextField input = ((TextField) children.get(i+1));
					List<String> the_current_constraint = constraint.findObservation(constraint.getEndObservations(), o_name);
					input.setText(the_current_constraint.get(1));
				}
			}
		} else {
			this.setType(STARTING_OBSERVATIONS);
		}
	}
	
	/**
	 * Don't save any changes made to the constraint when the cancel button is selected. 
	 */
	@FXML
	private void handleCancel() {
		if(type.equals(STARTING_OBSERVATIONS)) {
			mainApp.getModels().clearNewestConstraint();
			mainApp.clearCenter();
			if(mainApp.getModels().getConstraints().size() > 0) {
				mainApp.showConstraints();
			}
		} else {
			CONSTRAINTType constraint = mainApp.getModels().getNewestConstraint();
			constraint.removeObservations();
			switchTypes();
		}
	}

}
