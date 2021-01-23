package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**creates configuration class,that handle getting and saving the configuration.<br>
 * see: {@link BaseConfiguration} for the restrictions of the class, class has to have empty constructor<br>
 * To access the cache use {@link #getConf(String, Class)}
 * @param <T> the type of configuration, class must implement {@link BaseConfiguration}
 * @author Or Man
 * @version 1.0
 * @since 21/01/2021
 */
public class Configuration<T extends BaseConfiguration> {
	private String filePath;
	private Class<T> confClass;
	private T dummyObject;
	
	/**
	 * save all the configurations that the class imported, if {@link #runtimeUpdate} is set to false(default) it will look for the configuration and not try to import again
	 */
	private static Map<String,BaseConfiguration> cache = new HashMap<>();
	
	/**
	 * Sets the runtime evaluation of the configuration<br>
	 * If <b>false</b> the class will search for configuration that imported before<br>
	 * If <b>true</b> the class will always try to import the configuration from file system<br>
	 * <b>Default:</b> false
	 */
	public static boolean runtimeUpdate = false;
	/**
	 * Decides if to log the first import of configuration file<br>
	 * If <b>false</b> importing will never show any log on success<br>
	 * If <b>true</b> if not on {@link #runtimeUpdate}, will log the first import of configuration file<br>
	 * <b>Default:</b> true
	 */
	public static boolean firstReadLog = true;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	/**
	 * Create manager for handling the configuration files
	 * @param confClass - the class of the parameter &lt;T&gt;
	 * @throws InstantiationException - if the class &lt;T&gt; not containing empty constructor
	 * @throws IllegalAccessException - if the class's &lt;T&gt; empty constructor is private
	 */
	public Configuration(Class<T> confClass) throws InstantiationException, IllegalAccessException {
		this.confClass=confClass;
		dummyObject = confClass.newInstance();
		this.filePath = dummyObject.getFilePath();
	}
	
	/**
	 * Get the configuration from the file system, or from Cache if {@link #runtimeUpdate} is false
	 * @return the configuration Class that exists in the Path specified by {@link BaseConfiguration#getFilePath()}<br>
	 * if error occurred while getting the file it will return the default, specified by {@link BaseConfiguration#getDefault()}
	 * @throws ConfigurationException - if the Cache include file with the same path, but from different Class&lt;T&gt;
	 */
	public T get() {
		if(!runtimeUpdate) {
			BaseConfiguration base =cache.get(filePath);
			if(base!=null ) {
				try {
					return confClass.cast(base);
				}catch(ClassCastException e) {
					throw new ConfigurationException("duplicated configuration file names in store, for file name: "+filePath,e);
				}
			}
		}
		File c = new File(filePath);
		if (!c.exists()) {
			System.out.println("file \""+filePath+"\" not found, creating file...");
			store(getDefault());
			return getDefault();
		}
		try {
			T conf = gson.fromJson(new InputStreamReader(new FileInputStream(filePath), "UTF-8"),
					confClass);
			if(conf==null) {
				System.out.println("file \""+filePath+"\" was Empty, creating file...");
				store(getDefault());
				return getDefault();
			}
			cache.put(filePath, conf);
			if(firstReadLog && !runtimeUpdate) {
				System.out.println("the file \""+filePath+"\" imported successfully");
			}
			return conf;
		} catch (Exception e) {
			System.err.println("error while getting conf \""+filePath+"\", returned default\ncause by:");
			e.printStackTrace();
			return getDefault();
		}
		
	}
	
	/**
	 * Fetches the defaults from the Class&lt;T&gt; and cast it to the Type
	 * @return
	 */
	private T getDefault() {
		T def = confClass.cast(dummyObject.getDefault());
		cache.put(filePath, def);
		return def;
	}
	
	/**
	 * Saves new configuration to the location specified by {@link BaseConfiguration#getFilePath()}
	 * @param configuration - the configuration to save
	 */
	public void store(T configuration) {
		File c = new File(filePath);
		if (!c.exists()) {
			try {
				c.createNewFile();
			} catch (IOException e) {
				System.out.println("failed to create the file \""+filePath+"\"\ncause by:\"");
				e.printStackTrace();
				return;
			}
		}
		PrintWriter pw;
		try {
			pw = new PrintWriter(c);
			pw.write(gson.toJson(configuration, configuration.getClass()));

			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("failed to write to the file \""+filePath+"\"\ncause by:\"");
			e.printStackTrace();
			return;
		}
		System.out.println("the file \""+filePath+"\" created succesfully");
		System.out.println("Content of file:");
		System.out.println(gson.toJson(configuration, confClass));
	}

	/**<b>runtimeUpdate</b> -  Sets the runtime evaluation of the configuration<br>
	 * If <b>false</b> the class will search for configuration that imported before<br>
	 * If <b>true</b> the class will always try to import the configuration from file system<br>
	 * <b>Default:</b> false
	 * @return the runtimeUpdate
	 */
	public static boolean isRuntimeUpdate() {
		return runtimeUpdate;
	}

	/**<b>runtimeUpdate</b> -  Sets the runtime evaluation of the configuration<br>
	 * If <b>false</b> the class will search for configuration that imported before<br>
	 * If <b>true</b> the class will always try to import the configuration from file system<br>
	 * <b>Default:</b> false
	 * @param runtimeUpdate the runtimeUpdate to set
	 */
	public static void setRuntimeUpdate(boolean runtimeUpdate) {
		Configuration.runtimeUpdate = runtimeUpdate;
	}

	/**<b>firstReadLog</b> - Decides if to log the first import of configuration file<br>
	 * If <b>false</b> importing will never show any log on success<br>
	 * If <b>true</b> if not on {@link #runtimeUpdate}, will log the first import of configuration file<br>
	 * <b>Default:</b> true
	 * @return the firstReadLog
	 */
	public static boolean isFirstReadLog() {
		return firstReadLog;
	}

	/**<b>firstReadLog</b> - Decides if to log the first import of configuration file<br>
	 * If <b>false</b> importing will never show any log on success<br>
	 * If <b>true</b> if not on {@link #runtimeUpdate}, will log the first import of configuration file<br>
	 * <b>Default:</b> true
	 * @param firstReadLog the firstReadLog to set
	 */
	public static void setFirstReadLog(boolean firstReadLog) {
		Configuration.firstReadLog = firstReadLog;
	}
	
	/**
	 * Method to receive the Configuration from the cache
	 * @param <E> the Type of the received configuration
	 * @param filePath - the path to the configuration file
	 * @param clazz - the Class of the wanted configuration
	 * @return The configuration if exists or null otherwise
	 */
	public static <E> E getConf(String filePath,Class<E> clazz) {
		BaseConfiguration base =cache.get(filePath);
		if(base!=null ) {
			try {
				return clazz.cast(base);
			}catch(ClassCastException e) {
				throw new ConfigurationException("Wrong configuration class, for file: "+filePath,e);
			}
		}
		return null;
		
	}
}
