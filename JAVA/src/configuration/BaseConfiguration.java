package configuration;

public interface BaseConfiguration {
	
	String getFilePath();
	
	default BaseConfiguration getDefault() {
		return this;
	}
	
}
