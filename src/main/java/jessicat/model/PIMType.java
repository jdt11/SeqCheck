package jessicat.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author jessicaturner
 * 
 * The PIMType allows a conversion of the PIM python file to java for processing in the ProB2 tooling template. 
 *
 */
public interface PIMType extends SimpleType {
	
	//A function which returns the domain of the relations
	public HashSet<ArrayList<String>> getStatesFromRelations(HashSet<ArrayList<String>> relations);

	public String getNextState(String current_state, String beh);

	public int compareState(String current_state);
	
	public void setStartState(String state);
	
	public Set<String> getQ();
	
}
