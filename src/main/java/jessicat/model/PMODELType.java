package jessicat.model;

import java.util.List;

/**
 * A container for converting Python to Java for the PModel class. 
 * @author jessicaturner
 *
 */

public interface PMODELType extends SimpleType {

	public List<String> getBehaviours(String widget, String current_state);

	public List<String> getAllBehaviours();

	public List<String> getWidgets();

	public List<String> getAllSBehaviours();

}
