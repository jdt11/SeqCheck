package jessicat.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author jessicaturner
 *
 * The FSAType class allows us to pass information from the python object FSA to Java. 
 */

public interface FSAType extends SimpleType {
	
	public HashSet<String> alphabet();
	public HashSet<String> getStatesFromDelta(HashSet<ArrayList<String>> delta);
	public Set<String> getQ();
	public Set<String> getStarting();
	public Set<String> getAccepting();
	public Set<String> getSigma();
	public Set<List<String>> getDelta();
	public void addState(String state);
	public void addLetter(String letter);
	public void addStart(String state);
	public void addAccept(String state);
	public void addTriple(String q, String x, String qd);
	public Set<List<String>> getTriples(String state);
	public List<String> getWidgets();
	
}
