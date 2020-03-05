package jessicat.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import jessicat.modelChecker.MainApp;

public class ConsRunController {
	
	@FXML TextFlow runText;
	
	private MainApp mainApp;
	
	public ConsRunController() {
		
	}
	
	@FXML
	private void initialise() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		//Run the constraints. 
		mainApp.getModels().runConstraints(runText);
	}
	
	@FXML
	private void handleSaveResults() throws IOException {
		System.err.println("Handle Save Results");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Results");
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
		if(file != null) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			ObservableList<Node> text = runText.getChildren();
			for(Node n: text) {
				if(n instanceof Text) {
					bw.write(((Text) n).getText());
				}
			}
			bw.close();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Saved Results");
			alert.setContentText("Your results have been saved.");
			alert.showAndWait();
		}
	}
	
	@FXML
	private void handleOk() {
		System.err.println("Handle Results");
		mainApp.clearCenter();
		mainApp.showConstraints();
	}

}
