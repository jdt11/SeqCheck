package jessicat.view;

import java.util.List;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import jessicat.model.CONSTRAINTType;
import jessicat.modelChecker.MainApp;

public class ConsViewController {
	
	@FXML 
	Label consName;
	
	@FXML
	ListView startList;
	
	@FXML
	ListView seqList;
	
	@FXML
	ListView endList;
	
	private MainApp mainApp;
	private CONSTRAINTType constraint;
	
	public ConsViewController() {
		
	}
	
	@FXML
	private void initialise() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setConstraint(String title) {
		consName.setText(title);
		constraint = mainApp.getModels().getConstraint(title);
		display();
	}
	
	private void display() {
		startList.getItems().clear();
		seqList.getItems().clear();
		endList.getItems().clear();
		Set<List<String>> starts = constraint.getStartObservations();
		for(List<String> s: starts) {
			String s_display = s.get(0) + ": " + s.get(1);
			startList.getItems().add(s_display);
		}
		Set<List<String>> ends = constraint.getEndObservations();
		for(List<String> e: ends) {
			String e_display = e.get(0) + ": " + e.get(1);
			endList.getItems().add(e_display);
		}
		List<String> seq = constraint.getSequence();
		for(String s: seq) {
			seqList.getItems().add(s);
		}
		
	}
	
	@FXML
	public void handleEdit() {
		Set<List<String>> observations = mainApp.getModels().getZModel().getObservations();
		mainApp.getModels().getConstraints().remove(constraint);
		mainApp.getModels().getConstraints().add(constraint);
		mainApp.showObservations(observations, constraint);
	}
	
	@FXML
	public void handleOk() {
		mainApp.clearRight();
	}
	

}
