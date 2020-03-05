package jessicat.view;

import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import jessicat.model.FSAType;
import jessicat.modelChecker.MainApp;

public class SeqInputController {
	
	private MainApp mainApp;
	private FSAType fsa;
	private int add_index = 0;
	
	@FXML 
	public ListView inputList;
	
	@FXML
	public ListView sequenceList;
	
	public SeqInputController() {
	}

	@FXML
	private void initialise() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	/**
	 * A class which specifies the FSA to use in order to input sequences. 
	 * @param fsa the FSA to use
	 */
	public void setFSA(FSAType fsa) {
		this.fsa = fsa;
		//If an inputList option is clicked then add this to the sequence and display the next lot of availab
		inputList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				String item = inputList.getSelectionModel().getSelectedItem().toString();
				sequenceList.getItems().add(add_index, item);
				add_index++;
				displayNextSteps(item,2);
			}
		});
		sequenceList.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.BACK_SPACE)) {
					changeDisplayIndex();
					sequenceList.getItems().remove(sequenceList.getSelectionModel().getSelectedIndex());
				}	
			}
		});
		sequenceList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				changeDisplayIndex();
			}
		});
		inputList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		sequenceList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	private void changeDisplayIndex() {
		int index = sequenceList.getSelectionModel().getSelectedIndex();
		String step = (String) sequenceList.getSelectionModel().getSelectedItem();
		displayNextSteps(step,0);
		add_index = index;
	}
	
	private void displayNextSteps(String step, int index) {
		String [] items = step.replaceAll("\\(", "").replaceAll("\\)","").replaceAll(" ","").split(",");
		Set<List<String>> new_triples = fsa.getTriples(items[index].trim());
		ObservableList<String> triples = getList(new_triples);
		inputList.setItems(triples);
	}
	
	@FXML
	private void handleOk() {
		mainApp.getModels().getNewestConstraint().setSequence(sequenceList.getItems());
		mainApp.clearCenter();
		mainApp.showConstraints();
		//System.out.println(mainApp.getModels().getNewestConstraint().toString());
	}
	
	@FXML
	private void handleCancel() {
		mainApp.showObservations(mainApp.getModels().getZModel().getObservations(), mainApp.getModels().getNewestConstraint());
	}
	
	public ObservableList<String> getList(Set<List<String>> triplesToDisplay){
		ObservableList<String> triples = FXCollections.observableArrayList();
		for(List<String> triple : triplesToDisplay) {
			String display = mainApp.getModels().prettify(triple.toString()).replaceAll("\'", "");
			triples.add(display);
		}
		return triples;
	}

}
