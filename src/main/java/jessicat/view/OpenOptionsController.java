package jessicat.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import jessicat.model.Models;
import jessicat.modelChecker.MainApp;

public class OpenOptionsController {

	private MainApp mainApp;
	private String PATH = System.getProperty("user.dir") + "/src/main/resources";
	
	public OpenOptionsController() {
		
	}
	
	@FXML
	private void initialise() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void handleOpenProject() throws Exception {
		String project_home = mainApp.getProjectHome();
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Open a Project");
		dirChooser.setInitialDirectory(new File(project_home));
		File file = dirChooser.showDialog(mainApp.getPrimaryStage());
		if(file != null) {
			System.err.println("You have chosen the file: " + file.toString());
			mainApp.openProject(file); 
		}
	}
	
	/**
	 * Create a new project folder for storing the model checker information. 
	 * @throws Exception
	 */
	@FXML
	private void handleNewProject() throws Exception {
		String IC_file;
		String FC_Z_file;
		String i_sys;
		String FC_B_file;
		
		File PIMed = openFile("PIMed", ".xml");
		if(PIMed==null) {
			handleCancel();
		} else {
			IC_file = PIMed.toString();
			File Z = openFile("Z",".tex");
			if(Z==null) {
				handleCancel();
			} else {
				FC_Z_file = Z.toString();
				i_sys = showNameDialog(Z);
				while(i_sys==null) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Interactive System Name Error");
					alert.setHeaderText("Interactive system name cannot be blank.");
					alert.setContentText("Please try again.");
					alert.showAndWait();
					i_sys = showNameDialog(Z);
				}
				File B = openFile("B",".mch");
				if(B==null) {
					handleCancel();
				} else {
					FC_B_file = B.toString();
					Models models = new Models(IC_file,FC_Z_file,i_sys,FC_B_file,mainApp);
					mainApp.setModels(models);
					showSequenceDialog();
				}
			}
		}
	}
	
	private void handleCancel() {
		mainApp.showOpenScreen();
		mainApp.clearModels();
	}
	
	/**
	 * Get the schema names from the Z specification. 
	 * @param FC_Z_file
	 * @return
	 * @throws Exception
	 */
	private ArrayList<String> findSchemaNames(File FC_Z_file) throws Exception {
		ArrayList<String> names = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(FC_Z_file));
		String line;
		while((line = br.readLine())!=null) {
			if(line.contains("\\begin{schema}")) {
				int end = line.length()-1;
				String schemaName = line.substring(15,end);
				names.add(schemaName);
			}
		}
		br.close();
		return names;
	}
	
	/**
	 * Display a dialog to allow the user to specify the name of the interactive system in the Z specification. 
	 * @param FC_Z_file
	 * @return
	 * @throws Exception
	 */
	private String showNameDialog(File FC_Z_file) throws Exception {
		ArrayList<String> choices = findSchemaNames(FC_Z_file);
		ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
		dialog.setTitle("Interactive System Name");
		dialog.setHeaderText("Please select the interactive system name:");
		
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent() && !result.get().equals("")) {
			return result.get();
		}
		return null;
	}
	
	/**
	 * Ask the user whether they would like to load a pre-existing interaction sequence file or enter a new sequence from the model. 
	 * @throws Exception
	 */
	
	private void showSequenceDialog() throws Exception {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Open FSA?");
		alert.setContentText("Would you like to open an FSA file or enter an interaction sequence?");
		
		ButtonType buttonOpen = new ButtonType("Open");
		ButtonType buttonEnter = new ButtonType("Enter");
		ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(buttonOpen,buttonEnter,buttonCancel);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == buttonOpen) {
			File FSA_file = openFile("FSA", ".fsa");
			if(FSA_file==null) {
				handleCancel();
			} else {
				Models models = mainApp.getModels();
				models.loadFSAFile(FSA_file);
				mainApp.clearLayout();
				models.createNewProject(models.getZModel().getSystemName());
				mainApp.showToolbar();
			}
		} else if (result.get() == buttonEnter) {
			mainApp.showEnterSeqScreen();
		} else {
			handleCancel();
		}
		
	}
	
	/**
	 * Allow an end-user to open a specific file. 
	 * @param message
	 * @param type
	 * @return
	 */
	private File openFile(String message, String type) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open " + message + " file");
		fileChooser.setInitialDirectory(new File(PATH));
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(message + " files", "*" + type)
		);
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		if(file != null) {
			System.err.println("You have chosen the file: " + file.toString());
			return file;
		} 
		return null;
	}
}
