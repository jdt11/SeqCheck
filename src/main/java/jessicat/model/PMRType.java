package jessicat.model;

import java.util.List;

/**
 * A container for converting Python to Java for the PMR class. 
 * @author jessicaturner
 *
 */
public interface PMRType extends SimpleType {

	String getBehaviour(String beh);

	List<List<String>> getAllRelations();
	List<String> getDomain();

	List<String> getRange();

}
