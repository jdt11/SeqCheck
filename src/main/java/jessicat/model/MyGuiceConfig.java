package jessicat.model;

import com.google.inject.AbstractModule;

import de.prob.MainModule;

/**
 * 
 * @author jessicaturner
 *
 * From the ProB2 tooling template package, allows injection bindings. 
 */
public class MyGuiceConfig extends AbstractModule {

	@Override
	protected void configure() {
		install(new MainModule()); // Install ProB 2.0 Injection bindings
	}

}
