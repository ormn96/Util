package configuration;

/**
 * Interface for Configuration class, contains {@link #getFilePath()}, {@link #getDefault()}<br>
 * this class need to have empty constructor for usage by {@link Configuration}
 * @author Or Man
 * @version 1.0
 * @since 21/01/2021
 */
public interface BaseConfiguration {
	
	/**
	 * @return the Path to the configurationFile
	 */
	String getFilePath();
	
	/**
	 * Get the default values of the configuration file, base operation returns instance of the empty constructor
	 * @return the Defaults
	 */
	default BaseConfiguration getDefault() {
		return this;
	}
	
}
