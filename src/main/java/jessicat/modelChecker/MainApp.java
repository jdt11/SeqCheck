package jessicat.modelChecker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jessicat.model.CONSTRAINTType;
import jessicat.model.FSAType;
import jessicat.model.Models;
import jessicat.view.ConsController;
import jessicat.view.ConsRunController;
import jessicat.view.ConsViewController;
import jessicat.view.EnterSeqController;
import jessicat.view.ObservationsController;
import jessicat.view.OpenOptionsController;
import jessicat.view.SeqInputController;
import jessicat.view.ToolbarController;

/**
 * This class stores the information for the main application, using Java FX.  
 * @author jessicaturner
 *
 */
public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	public String PATH = "file:" + System.getProperty("user.dir") + "/src/main/java/jessicat/";
	private Models models;
	private String PROJECT_HOME = System.getProperty("user.home") + "/Documents/Model Checker Projects/";
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Model Checker");
		
		initRootLayout();
		
		showOpenScreen();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/RootLayout.fxml");
			System.out.println(url);
			loader.setLocation(url);
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showOpenScreen() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/openOptions.fxml");
			loader.setLocation(url);
			AnchorPane openOptions = (AnchorPane) loader.load();
			
			rootLayout.setCenter(openOptions);
			
			OpenOptionsController controller = loader.getController();
			controller.setMainApp(this);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showEnterSeqScreen() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/EnterSeq.fxml");
			loader.setLocation(url);
			AnchorPane EnterSeq = (AnchorPane) loader.load();
			
			rootLayout.setCenter(EnterSeq);
			
			EnterSeqController controller = loader.getController();
			controller.setMainApp(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showToolbar() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/toolbar.fxml");
			loader.setLocation(url);
			AnchorPane toolbar = (AnchorPane) loader.load();
			
			rootLayout.setTop(toolbar);
			
			ToolbarController controller = loader.getController();
			controller.setMainApp(this);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showObservations(Set<List<String>> observations, CONSTRAINTType constraint) {
		try {
			clearCenter();
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/observations.fxml");
			loader.setLocation(url);
			AnchorPane observationsView = (AnchorPane) loader.load();
			rootLayout.setCenter(observationsView);
			ObservationsController controller = loader.getController();
			controller.setMainApp(this);
			controller.setType("start");
			
			int x = 0;
			int y = 0;
			Set<List<String>> starting = constraint.getStartObservations();
			//Get the observations and display them to the end-user
			if(starting.size()<= 0) {
				for(List<String> o: observations) {
					Label the_label = new Label(o.get(0) + ":");
					the_label.setPadding(new Insets(10));
					TextField the_data = new TextField(o.get(1));
					the_data.setMaxWidth(150.0);
					controller.myGridPane.add(the_label, x, y);
					controller.myGridPane.add(the_data, x+1, y);
					y++;
				}
			} else {
				for(List<String> s: starting) {
					Label the_label = new Label(s.get(0) + ":");
					the_label.setPadding(new Insets(10));
					TextField the_data = new TextField(s.get(1));
					the_data.setMaxWidth(150.0);
					controller.myGridPane.add(the_label, x, y);
					controller.myGridPane.add(the_data, x+1, y);
					y++;
				}
			}
			//}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showSeqInput() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/seqinput.fxml");
			loader.setLocation(url);
			AnchorPane seqinputView = (AnchorPane) loader.load();
			rootLayout.setCenter(seqinputView);
			SeqInputController controller = loader.getController();
			controller.setMainApp(this);
			
			CONSTRAINTType constraint = models.getNewestConstraint();
			FSAType fsa = models.getFSA();
			controller.setFSA(fsa);
			Set<String> starting = null;
			List<String> sequence = constraint.getSequence();
			//If the sequence is empty then get the first step in the sequence
			if(sequence.size()<=0) {
				starting = fsa.getStarting();
			//Otherwise get the set of "next steps" based on the last widget that was interacted with
			} else {
				String last_step = sequence.get(sequence.size()-1);
				String [] parts = last_step.replaceAll("\\(", "").replaceAll("\\)","").replaceAll(" ", "").split(",");
				//Set<String> h = new HashSet<>(Arrays.asList("a", "b"));
				starting = new HashSet<String>();
				starting.add(parts[2]);
				
				ObservableList<String> seq = FXCollections.observableArrayList();
				for(String step: sequence) {
					seq.add(step);
				}
				controller.sequenceList.setItems(seq);
			}
			Set<List<String>> triplesToDisplay = null;
			for(String s: starting) {
				Set<List<String>> triples = fsa.getTriples(s);
				if(triplesToDisplay == null) {
					triplesToDisplay = triples;
				} else {
					triplesToDisplay.addAll(triples);
				}
			}
			ObservableList<String> triples = controller.getList(triplesToDisplay);
			controller.inputList.setItems(triples);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showConstraints() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/constraints.fxml");
			loader.setLocation(url);
			AnchorPane constraintsView = (AnchorPane) loader.load();
			rootLayout.setLeft(constraintsView);
			ConsController controller = loader.getController();
			controller.setMainApp(this);
			controller.display();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void showConsView(String selectedItem) {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/ConsView.fxml");
			loader.setLocation(url);
			AnchorPane constraintsView = (AnchorPane) loader.load();
			rootLayout.setRight(constraintsView);
			ConsViewController controller = loader.getController();
			controller.setMainApp(this);
			controller.setConstraint(selectedItem);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void showConsRunner() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = new URL(PATH + "view/constraintRunner.fxml");
			loader.setLocation(url);
			AnchorPane constraintRunnerView = (AnchorPane) loader.load();
			rootLayout.setCenter(constraintRunnerView);
			ConsRunController controller = loader.getController();
			controller.setMainApp(this);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void clearLayout() {
		rootLayout.setTop(null);
		rootLayout.setCenter(null);
		rootLayout.setLeft(null);
		rootLayout.setRight(null);
	}
	
	public void clearCenter() {
		rootLayout.setCenter(null);
		rootLayout.setLeft(null);
		rootLayout.setRight(null);
	}
	
	public void clearRight() {
		rootLayout.setRight(null);
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public Models getModels() {
		return models;
	}
	
	public void setModels(Models models) {
		this.models = models;
	}
	
	public void clearModels() {
		this.models = null;
	}

	public String getProjectHome() {
		return PROJECT_HOME;
	}

	public void openProject(File file) throws Exception {
		String path = file.getAbsolutePath();
		
		String system_name = file.getName();
		String IC_file = path + "/Interactive/" + system_name + ".xml";
		String FC_Z_file = path + "/Functional/" + system_name + ".tex";
		String FC_B_file = path + "/Functional/" + system_name + ".mch";
		
		models = new Models(IC_file,FC_Z_file,system_name,FC_B_file, this);
		
		String fsa_path = path + "/Overlap/FSA/" + system_name + ".fsa";
		String c_path = path + "/Overlap/Constraints/constraints.xml";
		
		models.loadFSAandConstraints(fsa_path,c_path);
		
		clearLayout();
		showToolbar();
		showConstraints();
		
	}

}
