package jessicat.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import jessicat.modelChecker.MainApp;

/**
 * 
 * @author jessicaturner
 * 
 * This class pulls together all the different types of models needed in order to explore the overlap properties. 
 *
 */
public class Models {
	
	private FSAType fsa;
	private PIMType pim;
	private PMODELType pmodel;
	private PMRType pmr;
	private ZType zManager;
	private FuncSpec bManager;
	
	private String fsa_file_name;
	private String PIMed_file_name;
	private String z_file_name;
	private String b_file_name;
	
	private ArrayList<CONSTRAINTType> constraints = new ArrayList<CONSTRAINTType>();
	private String current_state; 
	
	private MainApp mainApp;
	
	//private String PATH = System.getProperty("user.dir") + "/src/main/resources/";
	private static Injector INJECTOR = Guice.createInjector(Stage.PRODUCTION, new MyGuiceConfig());
	
	public Models(String IC_file, String FC_Z_file, String i_sys, String FC_B_file, MainApp m) throws Exception {
		if(IC_file != null && FC_Z_file != null && FC_B_file != null && m != null) {
			//loadInteractiveComponent(PATH + IC_file);
			//loadFunctionalComponent(PATH + FC_Z_file, i_sys, PATH + FC_B_file);
			PIMed_file_name = IC_file;
			z_file_name = FC_Z_file;
			b_file_name = FC_B_file;
			loadInteractiveComponent(IC_file);
			loadFunctionalComponent(FC_Z_file, i_sys, FC_B_file);
			mainApp = m;
		}
	}
	
	public ZType getZModel() {
		return zManager;
	}
	
	public FSAType getFSA() {
		return fsa;
	}
	
	public String getState() {
		return current_state;
	}
	
	public boolean createNewProject(String name) throws Exception {
		String path = mainApp.getProjectHome() + name;
		Path p = Paths.get(path);
		String system_name = zManager.getSystemName();
		if(Files.exists(p)) {
			System.err.println("Project " + name + " already exists.");
			return false;
		} else {
			System.out.println(path);
			new File(path).mkdirs();
			
			File interactive = new File(path + "/Interactive");
			interactive.mkdir();
			newInteractive(PIMed_file_name,path,system_name);
			
			File functional = new File(path + "/Functional");
			functional.mkdir();
			newFunctional(z_file_name,b_file_name,path,system_name);
			
			new File(path + "/Overlap").mkdir();
			new File(path + "/Overlap/Constraints").mkdir();
			File fsa = new File(path + "/Overlap/FSA/");
			fsa.mkdir();
			createFSAFile(system_name, fsa);
		}
		return true;
	}
	
	public void newInteractive(String file, String path, String system_name) throws Exception {
		String new_path = path + "/Interactive/" + system_name + ".xml";
		Files.copy(Paths.get(file), Paths.get(new_path), StandardCopyOption.REPLACE_EXISTING);
		if(!PIMed_file_name.equals(new_path)) {
			PIMed_file_name = new_path;
			loadInteractiveComponent(new_path);
		}
		
	}
	
	public void newFunctional(String z_file, String b_file, String path, String system_name) throws Exception {
		newZFile(z_file,path,system_name);
		newBFile(b_file,path,system_name);
	}
	
	public void newZFile(String z_file, String path, String system_name) throws IOException {
		String new_path = path + "/Functional/" + system_name + ".tex";
		Files.copy(Paths.get(z_file), Paths.get(new_path), StandardCopyOption.REPLACE_EXISTING);
		if(!z_file_name.equals(new_path)) {
			z_file_name = new_path;
			loadZ(new_path,system_name);
		}
	}
	
	public void newBFile(String b_file, String path, String system_name) throws Exception {
		String new_path = path + "/Functional/" + system_name + ".mch";
		Files.copy(Paths.get(b_file), Paths.get(new_path), StandardCopyOption.REPLACE_EXISTING);
		if(!b_file_name.equals(new_path)) {
			b_file_name = new_path;
			loadB(new_path);
		}
	}
	
	private void createFSAFile(String system_name, File fsa_file) throws IOException {
		String file_name = fsa_file.toString() + "/" + system_name + ".fsa";
		System.err.println("The file_name: " + file_name);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file_name)));
		bw.write("<FSA>\n");
		
		bw.write("\t<States>\n");
		Set<String> Q = fsa.getQ();
		for(String q : Q) {
			bw.write("\t\t<state>" + q + "</state>\n");
		}
		bw.write("\t</States>\n");
		
		bw.write("\t<Alphabet>\n");
		Set<String> Sigma = fsa.getSigma();
		for(String e: Sigma) {
			bw.write("\t\t<word>" + e + "</word>\n");
		}
		bw.write("\t</Alphabet>\n");
		
		bw.write("\t<Delta>\n");
		Set<List<String>> delta = fsa.getDelta();
		for(List<String> t: delta) {
			bw.write("\t\t<Triple>\n");
			bw.write("\t\t\t<q>" + t.get(0) + "</q>\n");
			bw.write("\t\t\t<x>" + t.get(1) + "</x>\n");
			bw.write("\t\t\t<qd>" + t.get(2) + "</qd>\n");
			bw.write("\t\t</Triple>\n");
		}
		bw.write("\t</Delta>\n");
		
		bw.write("\t<StartingStates>\n");
		Set<String> starting = fsa.getStarting();
		for(String s: starting) {
			bw.write("\t\t<start>" + s + "</start>\n");
		}
		bw.write("\t</StartingStates>\n");
		
		bw.write("\t<AcceptingStates>\n");
		Set<String> accepting = fsa.getAccepting();
		for(String a: accepting) {
			bw.write("\t\t<accept>" + a + "</accept>\n");
		}
		bw.write("\t</AcceptingStates>\n");
		
		bw.write("</FSA>\n");
		bw.close(); 
	}
	
	public void loadFSAFile(File fsa_file) throws Exception {
		System.err.println("loadFSAFile(" + fsa_file.getName() + ")");
		this.fsa_file_name = fsa_file.getAbsolutePath();
		
		JythonObjectFactory factory = new JythonObjectFactory(FSAType.class, "FSA", "FSA");
		fsa = (FSAType) factory.createObject("");
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fsa_file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("state");
		for(int i = 0; i < nList.getLength(); i++) {
			fsa.addState(nList.item(i).getTextContent());
		}
		nList = doc.getElementsByTagName("word");
		for(int i=0; i < nList.getLength(); i++) {
			fsa.addLetter(nList.item(i).getTextContent());
		}
		nList = doc.getElementsByTagName("start");
		for(int i=0; i < nList.getLength(); i++) {
			fsa.addStart(nList.item(i).getTextContent());
		}
		nList = doc.getElementsByTagName("accept");
		for(int i=0; i < nList.getLength(); i++) {
			fsa.addAccept(nList.item(i).getTextContent());
		}
		nList = doc.getElementsByTagName("Triple");
		for(int i=0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			
			if(nNode.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String q = eElement.getElementsByTagName("q").item(0).getTextContent();
				String x = eElement.getElementsByTagName("x").item(0).getTextContent();
				String qd = eElement.getElementsByTagName("qd").item(0).getTextContent();
				fsa.addTriple(q, x, qd);
			}
		}
	}
	
	private void loadConstraintsFile(File file) throws Exception {
		JythonObjectFactory factory = new JythonObjectFactory(CONSTRAINTType.class, "CONSTRAINTS", "Constraint");
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("Constraint");
		for(int i = 0; i < nList.getLength(); i++) {
			
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String constraint_name = eElement.getElementsByTagName("name").item(0).getTextContent();
				System.out.println("CONSTRAINT: " + constraint_name);
				CONSTRAINTType constraint = (CONSTRAINTType) factory.createObject(constraint_name);
				
				NodeList startingNodes = eElement.getElementsByTagName("Starting");
				loadObservations(startingNodes, constraint, true);
				
				NodeList endingNodes = eElement.getElementsByTagName("Ending");
				loadObservations(endingNodes, constraint, false);
				
				List<String> the_sequence = new ArrayList<String>();
				NodeList sequenceNodes = eElement.getElementsByTagName("Sequence");
				for(int j = 0; j < sequenceNodes.getLength(); j++) {
					Node sn = sequenceNodes.item(j);
					NodeList steps = sn.getChildNodes();
					for(int k = 0; k < steps.getLength(); k++) {
						Node step_node = steps.item(k);
						if(step_node.getNodeType()==Node.ELEMENT_NODE) {
							String step = step_node.getTextContent().trim();
							the_sequence.add(step);
						}
					}
				}
				constraint.setSequence(the_sequence);
				mainApp.getModels().getConstraints().add(constraint);
			}
			//String constraint_name = nList.item(0).getTextContent();
			//CONSTRAINTType constraint = (CONSTRAINTType) factory.createObject(constraint_name);
			//mainApp.getModels().getConstraints().add(constraint);
		}
		
	}
	
	private void loadObservations(NodeList nodes, CONSTRAINTType constraint, boolean indicator) {
		for(int j = 0; j < nodes.getLength(); j++) {	
			Node sn = nodes.item(j);
			NodeList observations = sn.getChildNodes();
			for(int k=0; k < observations.getLength(); k++) {
				Node o = observations.item(k);
				if(o.getNodeType()==Node.ELEMENT_NODE) {
					Element oe = (Element) o;
					String o_name = oe.getElementsByTagName("oName").item(0).getTextContent();
					String o_value = oe.getElementsByTagName("oValue").item(0).getTextContent();
					constraint.addObservation(o_name, o_value, indicator);
				}
			}
		}
	}
	
	/**
	 * A method which allows us to open each associated file for each model. 
	 * @param IC_file: the XML file from the PIMed tool.
	 * @param FC_Z_file: the .tex file of the Z spec from the ProB Model Checker tool. 
	 * @param FC_B_file: the .mch file of the B spec from the ProB Model Checker tool.
	 * @param sequence: the interaction sequence specified by the user. 
	 * @throws Exception: to catch issues with opening files. 
	 */
	/*public Models(String IC_file, String FC_Z_file, String i_sys, String FC_B_file, String sequence) throws Exception {
		if(IC_file != null && FC_Z_file != null && FC_B_file != null) {
			//loadInteractiveComponent(PATH + IC_file);
			//loadFunctionalComponent(PATH + FC_Z_file, i_sys, PATH + FC_B_file);
			loadInteractiveComponent(IC_file);
			loadFunctionalComponent(FC_Z_file, i_sys, FC_B_file);
			initialiseFSA(sequence);
		}
	}*/
	
	public void initialiseFSA(String sequence) {
		JythonObjectFactory factory = new JythonObjectFactory(FSAType.class, "FSA", "FSA");
		fsa = (FSAType) factory.createObject(sequence);
		//System.out.println(prettify(fsa.toString()));
	}

	/**
	 * A method to process the XML file from the PIMed tool.
	 * @param file: the XML file from the PIMed tool.
	 * @throws Exception: to catch issues with opening files. 
	 */
	private void loadInteractiveComponent(String file) throws Exception {
		JythonObjectFactory factory = new JythonObjectFactory(PIMType.class, "PIM", "PIM");
		pim = (PIMType) factory.createObject(file);
		selectPIMStartState();
		//System.out.println(prettify(pim.toString()));
		factory = new JythonObjectFactory(PMODELType.class, "PMODEL", "PModel");
		pmodel = (PMODELType) factory.createObject(file);
		//System.out.println(prettify(pmodel.toString()));
		factory = new JythonObjectFactory(PMRType.class, "PMR", "PMR");
		pmr = (PMRType) factory.createObject(file);
		//System.out.println(prettify(pmr.toString()));
		current_state = "Init";
	}
	
	private void selectPIMStartState() {
		List<String> choices = new ArrayList<String>();
		choices.addAll(pim.getQ());
		
		ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
		dialog.setTitle("PIM Start State");
		dialog.setContentText("Select the start state for the PIM.");
		
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()) {
			pim.setStartState(result.get());
		}
	}
	
	/**
	 * A method to process the files for the functional component. 
	 * @param z_file: the .tex file of the Z spec from the ProB Model Checker tool. 
	 * @param b_file: the .mch file of the B spec from the ProB Model Checker tool. 
	 * @throws Exception: to catch issues with opening files. 
	 */
	private void loadFunctionalComponent(String z_file, String i_sys, String b_file) throws Exception {
		loadZ(z_file,i_sys);
		//System.out.println(prettify(zManager.toString()));
		loadB(b_file);
		//System.err.println("module=<module 'FuncSpec' from 'jessicat'>,class=<class 'FuncSpec.class'>");
		/*bManager.doTrace(null);
		bManager.doTrace(null);
		bManager.doTrace(null);
		bManager.printObservations();*/
		
	}
	
	private void loadZ(String z_file, String i_sys) {
		JythonObjectFactory factory = new JythonObjectFactory(ZType.class, "Z", "Manager");
		zManager = (ZType) factory.createObject(z_file,i_sys);
	}
	
	private void loadB(String b_file) throws Exception {
		bManager = INJECTOR.getInstance(FuncSpec.class);
		bManager.loadMachine(b_file);
	}
	
	public void printFSA() {
		System.out.println("FSA:");
		System.out.println(prettify(fsa.toString()));
	}
	
	
	public void dumpPrintModels(){
		System.out.println("PIM:");
		System.out.println(prettify(pim.toString()));
		System.out.println("PMODEL:");
		System.out.println(prettify(pmodel.toString()));
		System.out.println("PMR:");
		System.out.println(prettify(pmr.toString()));
		System.out.println("Z:");
		System.out.println(prettify(zManager.toString()));
		System.out.println("B:");
		System.out.println(bManager.toString());
	}
	
	public static String prettify(String the_string) {
		char delta_char = (char) 948;
		String delta_string = "" + delta_char;
		char sigma_char = (char) 931;
		String sigma_string = "" + sigma_char;
		return the_string.replaceAll("u'","'").replaceAll("\\\\u03B4", delta_string).replaceAll("\\\\u03A3", sigma_string);
	}
	
	public ArrayList<CONSTRAINTType> getConstraints(){
		return constraints;
	}
	
	public CONSTRAINTType getNewestConstraint() {
		int index = constraints.size()-1;
		return constraints.get(index);
	}
	
	public void clearNewestConstraint() {
		if(constraints.size() > 0) {
			constraints.remove(constraints.size()-1);
		}
	}
	
	public void dumpConstraints() {
		for(CONSTRAINTType c: constraints) {
			System.out.println(prettify(c.toString()));
			
		}
	}
	
	public CONSTRAINTType getConstraint(String name) {
		for(CONSTRAINTType c: constraints) {
			if(c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	private boolean subset(List<String> one, List<String> two) {
		boolean [] markers = new boolean[two.size()];
		Arrays.fill(markers, false);
		for(int i = 0; i < two.size(); i++) {
			String s1 = two.get(i);
			for(String s2: one) {
				if(s1.equals(s2)) {
					markers[i] = true;
				}
			}
		}
		System.out.println(Arrays.toString(markers));
		for(boolean m: markers) {
			if(m==false) {
				return false;
			}
		}
		return true;
		
	}

	public void runConstraints(TextFlow runText) {
		runText.getChildren().clear();
		runText.getChildren().add(new Text("--- Starting constraints ---\n"));
		if (constraints.size()<=0||constraints==null) {
			runText.getChildren().add(new Text("There are no constraints.\n---"));
		} else {
			//SOP1 condition i
			List<String> Q2 = fsa.getWidgets();
			List<String> pmodel_wid = pmodel.getWidgets();
			
			if(subset(pmodel_wid,Q2)) {
				runText.getChildren().add(new Text("The widgets match.\n"));
			} else {
				Text the_text = new Text("The widgets do not match: " + Arrays.toString(Q2.toArray()) + " and " + Arrays.toString(pmodel_wid.toArray()) + ".\n");
				the_text.setFill(Color.RED);
				runText.getChildren().add(the_text);
			}
			
			List<String> pmodel_behaviours = pmodel.getAllSBehaviours();
			List<String> domain = pmr.getDomain();
			
			if(subset(domain,pmodel_behaviours)) {
				runText.getChildren().add(new Text("The S-behaviours match.\n"));
			} else {
				Text the_text = new Text("The S-behaviours do not match: " + Arrays.toString(domain.toArray()) + " and " + Arrays.toString(pmodel_behaviours.toArray()) + ".\n");
				the_text.setFill(Color.RED);
				runText.getChildren().add(the_text);
			}

			List<String> range = pmr.getRange();
			List<String> z_behaviours = zManager.getSchemaNames();

			if(subset(z_behaviours,range)) {
				runText.getChildren().add(new Text("The Z schemas match.\n"));
			} else {
				Text the_text = new Text("The Z schemas do not match: " + Arrays.toString(z_behaviours.toArray()) + " and " + Arrays.toString(range.toArray()) + ".\n");
				the_text.setFill(Color.RED);
				runText.getChildren().add(the_text);
			}
			
			for(int i = 0; i < constraints.size(); i++) {
				CONSTRAINTType c = constraints.get(i);
				String name = c.getName();
				String index = Integer.toString(i+1);
				runText.getChildren().add(new Text("--- " + index + ". " + name + " ---\n"));
				bManager.reset();
				int start_count = bManager.compareObservations(c.getStartObservations(), runText);
				String start = c.findObservation(c.getStartObservations(),"state").get(1);
				//System.out.println("Start = " + start);
				int state_count = pim.compareState(start);
				if(state_count > 0) {
					Text the_text = new Text("The states do not match: " + start + " and " + current_state + ".\n");
					the_text.setFill(Color.RED);
					runText.getChildren().add(the_text);
					start_count += state_count;
				} else {
					runText.getChildren().add(new Text("The states match.\n"));
				}
				
				
				
				//bManager.doSequence(c.getSequence());
				runSequence(c.getSequence(), runText);
				
				int end_count = bManager.compareObservations(c.getEndObservations(), runText);
				String end = c.findObservation(c.getEndObservations(),"state").get(1);
				//System.out.println("End = " + end);
				//System.out.println("current_state = " + current_state);
				int state_end_count = pim.compareState(end);
				if(state_end_count > 0) {
					Text the_text = new Text("The states do not match: " + end + " and " + current_state + ".\n");
					the_text.setFill(Color.RED);
					runText.getChildren().add(the_text);
					end_count += state_end_count;
				} else {
					runText.getChildren().add(new Text("The states match.\n"));
				}
				
				Text sc_text = new Text("Errors found in starting observations: " + Integer.toString(start_count) + ".\n");
				sc_text.setFill(Color.RED);
				runText.getChildren().add(sc_text);
				
				Text ec_text = new Text("Errors found in ending observations: " + Integer.toString(end_count) + ".\n");
				ec_text.setFill(Color.RED);
				runText.getChildren().add(ec_text);
				
				int total_count = start_count + end_count;
				
				Text t_text = new Text("Total Errors in observations:  " + Integer.toString(total_count) + ".\n");
				t_text.setFill(Color.RED);
				runText.getChildren().add(t_text);
				runText.getChildren().add(new Text("---\n\n"));
			}
		}
		
	}
	
	public void runSequence(List<String> sequence, TextFlow runText) {
		for(String step: sequence) {
			runText.getChildren().add(new Text("Step: " + step + "\n"));
			String [] step_parts = step.substring(1, step.length()-1).split(",");
			//System.out.println("step_parts = " + Arrays.toString(step_parts));
			//System.out.println("current_state = " + current_state);
			List<String> behs = pmodel.getBehaviours(step_parts[2].trim(),current_state);
			if (behs != null) { 
				runText.getChildren().add(new Text("Behaviours are: " + Arrays.toString(behs.toArray()) + "\n"));
				//System.out.println("BEHS: " + Arrays.toString(behs.toArray()));
				for(String beh: behs) {
					String beh_type = beh.substring(0,2);
					//System.out.println("beh_type = " + beh_type);
					//System.out.println("beh_type = " + beh_type);
					if(beh_type.equals("I_")) {
						//System.out.println("I behaviour!");
						current_state = pim.getNextState(current_state,beh);
						pim.setStartState(current_state);
						runText.getChildren().add(new Text("Next state is: " + current_state + "\n"));
						runText.getChildren().add(new Text("Completed behaviour: " + beh + "\n"));
					} else if(beh_type.equals("S_")) {
						//System.out.println("S Behaviour!");
						String s_beh = pmr.getBehaviour(beh);
						bManager.doTrace(s_beh);
						runText.getChildren().add(new Text("Completed behaviour: " + s_beh + "\n"));
					}
				}
			}
			
		}
	}

	public boolean save() throws IOException {
		//check project exists
		String name = zManager.getSystemName();
		String path = mainApp.getProjectHome() + name;
		Path p = Paths.get(path);
		boolean file_created;
		if(Files.exists(p)) {
			System.out.println("Yes project exists!");
			String constraint_path = p.toString() + "/Overlap/Constraints";
			Path cp = Paths.get(constraint_path);
			if(Files.exists(cp)) {
				System.out.println("Constraints folder exists!");
				file_created = createConstraints(constraint_path);
			} else {
				file_created = false;
			}
		} else {
			file_created = false;
		}
		return file_created;
	}

	private boolean createConstraints(String path) throws IOException {
		File c_file = new File(path);
		String file_name = c_file.toString() + "/constraints.xml";
		System.err.println("The file_name: " + file_name);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file_name)));
		bw.write("<Constraints>\n");
		for(CONSTRAINTType c: constraints) {
			bw.write("\t<Constraint>\n");
			bw.write("\t\t<name>" + c.getName() + "</name>\n");
			
			bw.write("\t\t<Starting>\n");
			Set<List<String>> starting = c.getStartObservations();
			for(List<String> obs: starting) {
				bw.write("\t\t\t<Observation>\n");
				bw.write("\t\t\t\t<oName>" + obs.get(0) + "</oName>\n");
				bw.write("\t\t\t\t<oValue>" + obs.get(1) + "</oValue>\n");
				bw.write("\t\t\t</Observation>\n");
			}
			bw.write("\t\t</Starting>\n");
			
			bw.write("\t\t<Ending>\n");
			Set<List<String>> ending = c.getEndObservations();
			for(List<String> obs: ending) {
				bw.write("\t\t\t<Observation>\n");
				bw.write("\t\t\t\t<oName>" + obs.get(0) + "</oName>\n");
				bw.write("\t\t\t\t<oValue>" + obs.get(1) + "</oValue>\n");
				bw.write("\t\t\t</Observation>\n");
			}
			bw.write("\t\t</Ending>\n");
			
			bw.write("\t\t<Sequence>\n");
			List<String> seq = c.getSequence();
			for(String step: seq) {
				bw.write("\t\t\t<Step>" + step + "</Step>\n");
			}
			bw.write("\t\t</Sequence>\n");
			bw.write("\t</Constraint>\n");
		}
		bw.write("</Constraints>\n");
		bw.close();
		return true;
	}

	public void loadFSAandConstraints(String fsa_path, String c_path) throws Exception {
		loadFSAFile(new File(fsa_path));
		loadConstraintsFile(new File(c_path));
	}

}
