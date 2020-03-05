package jessicat.model;

import java.util.List;
import java.util.Set;

import org.python.antlr.ast.Tuple;

/**
 * A container for converting Python to Java for the Contraints class. 
 * @author jessicaturner
 *
 */

public interface CONSTRAINTType extends SimpleType {
	
	public String getName();
	public Set<List<String>> getStartObservations();
	public Set<List<String>> getEndObservations();
	public void addObservation(String obs_name, String obs_value, boolean indicator);
	public void removeObservation(Tuple obs, boolean indicator);
	public void modifyObservation(Tuple obs, boolean indicator);
	public void removeObservations();
	public void setSequence(List<String> sequence);
	public List<String> getSequence();
	public List<String> findObservation(Set<List<String>> startObservations, String string);

}
