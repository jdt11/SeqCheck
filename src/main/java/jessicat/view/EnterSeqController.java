package jessicat.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import jessicat.model.Models;
import jessicat.modelChecker.MainApp;

public class EnterSeqController {
	
	@FXML TextArea seqText;
	
	private MainApp mainApp;
	
	public EnterSeqController() {
		
	}
	
	@FXML
	private void initialise() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void handleOk() throws Exception {
		Models models = mainApp.getModels();
		String sequence = seqText.getText().trim();
		models.initialiseFSA(sequence);
		mainApp.clearLayout();
		models.createNewProject(models.getZModel().getSystemName());
		mainApp.showToolbar();
	}
	
	@FXML
	private void handleCancel() {
		mainApp.showOpenScreen();
	}

}
