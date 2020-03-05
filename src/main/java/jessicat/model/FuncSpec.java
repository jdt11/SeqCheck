package jessicat.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.scripting.Api;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * 
 * @author jessicaturner
 *
 * The functional specification class handles the processing of the B specification for the functional component using the ProB2 tool. 
 */
public class FuncSpec {

	private Api api;
	private Trace trace;
	private StateSpace stateSpace;

	@Inject
	public FuncSpec(Api api) {
		this.api = api;
	}
	
	public void loadMachine(String filename) throws Exception {
		stateSpace = api.b_load(filename);
		reset();
	}
	
	public Trace getTrace() {
		return trace;
	}
	
	public void setTrace(Trace t) {
		trace = t;
	}
	
	public void reset() {
		trace = new Trace(stateSpace);
		trace = trace.anyEvent(null);
	}
	
	public void printObservations() {
		State current_state = trace.getCurrentState();
		Map<IEvalElement, AbstractEvalResult> values = current_state.getValues();
		Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = values.entrySet();
		for (Entry<IEvalElement, AbstractEvalResult> entry : entrySet) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
		}
	}
	
	public void doTrace(String name) {
		trace = trace.anyEvent(name);
	}

	public int compareObservations(Set<List<String>> observations, TextFlow runText) {
		int error_count = 0;
		State current_state = trace.getCurrentState();
		Map<IEvalElement, AbstractEvalResult> values = current_state.getValues();
		Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = values.entrySet();
		runText.getChildren().add(new Text("Size of provided observations: " + Integer.toString(observations.size()) + "\n"));
		runText.getChildren().add(new Text("Size of B observations + state: " + (Integer.toString(entrySet.size()+1)) + "\n"));
		if(observations.size()-1==entrySet.size()) {
			runText.getChildren().add(new Text("Observation set size is equal.\n"));
		} else {
			Text neq = new Text("Observation set size is not equal.\n");
			neq.setFill(Color.RED);
			runText.getChildren().add(neq);
			error_count++;
		}
		for(List<String> so: observations) {
			String o_name = so.get(0);
			String o_value = so.get(1);
			boolean found = false;
			for(Entry<IEvalElement, AbstractEvalResult> entry: entrySet) {
				String b_name = entry.getKey().toString();
				String b_value = entry.getValue().toString();
				if(o_name.equals(b_name)) {
					found = true;
					runText.getChildren().add(new Text("Found observation " + o_name + "; "));
					if(o_value.equals(b_value)) {
						runText.getChildren().add(new Text("values are equal (" + o_name + "=" + o_value + ").\n"));
					} else {
						Text neqv = new Text("values are not equal.\n"
								+ o_name + "=" + o_value + " and " + o_name + "=" + b_value + ".\n");
						neqv.setFill(Color.RED);
						runText.getChildren().add(neqv);
						error_count++;
						
					}
					break;
				}
			}
			if (found==false&&!o_name.equals("state")) {
				Text nfound = new Text(o_name + " was not found.\n");
				nfound.setFill(Color.RED);
				runText.getChildren().add(nfound);
				error_count++;
			}
		}
		return error_count;
	}

	/*public void doSequence(String beh) {
			doTrace(beh);
	}

	/*public static void main(String[] args) throws Exception {

		FuncSpec m = INJECTOR.getInstance(FuncSpec.class);
		System.out.println("Please enter file name:");
		Scanner scan = new Scanner(System.in);
		String filename = scan.nextLine();
		m.loadMachine(filename);
		

		ArrayList<String> operations = new ArrayList<String>();
		operations.add("IncrementPump");
		operations.add("IncrementPump");
		operations.add("IncrementPump");
		operations.add("IncrementPump");
		operations.add("IncrementPump");
		operations.add("IncrementPump");
		operations.add("IncrementDur");
		operations.add("IncrementDur");
		operations.add("IncrementVol");
		operations.add("IncrementVol");
		operations.add("IncrementVol");
		operations.add("IncrementVol");
		operations.add("IncrementVol");
		operations.add("IncrementVol");
		operations.add("CalculateRate");
		for(String s: operations) {
			m.doTrace(s);
		}
		
		m.printObservations();
		
		System.exit(0);
	}*/
}