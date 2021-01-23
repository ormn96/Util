package configuration;

/**
 * Class for Exception in configuration
 * @author Or Man
 * @version 1.0
 * @since 21/01/2021
 * @see Configuration
 */
public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 2898378772037793964L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ConfigurationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConfigurationException(String arg0) {
		super(arg0);
	}

	public ConfigurationException(Throwable arg0) {
		super(arg0);
	}

	
}
