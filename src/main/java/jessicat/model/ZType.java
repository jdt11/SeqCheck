package jessicat.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author jessicaturner
 *
 * This class allows us to "join" Java and Python so that they can be used via Jython. 
 */
public interface ZType extends SimpleType {
	
	public List<String> getRandomSchemas(int num);
	public String getSystemName();
	public Set<List<String>> getObservations();
	public List<String> getSchemaNames();
}
