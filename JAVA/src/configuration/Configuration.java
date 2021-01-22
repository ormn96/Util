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

public class Configuration<T extends BaseConfiguration> {
	private String fileName;
	private Class<T> confClass;
	private T dummyObject;
	private static Map<String,BaseConfiguration> map = new HashMap<>();
	public static boolean runtimeUpdate = false;
	public static boolean firstReadLog = true;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	public Configuration(Class<T> confClass) throws InstantiationException, IllegalAccessException {
		this.confClass=confClass;
		dummyObject = confClass.newInstance();
		this.fileName = dummyObject.getFilePath();
	}
	
	public T get() {
		if(!runtimeUpdate) {
			BaseConfiguration base =map.get(fileName);
			if(base!=null ) {
				try {
					return confClass.cast(base);
				}catch(ClassCastException e) {
					throw new ConfigurationException("duplicated configuration file names in store, for file name: "+fileName,e);
				}
			}
		}
		File c = new File(fileName);
		if (!c.exists()) {
			System.out.println("file \""+fileName+"\" not found, creating file...");
			store(getDefault());
			return getDefault();
		}
		try {
			T conf = gson.fromJson(new InputStreamReader(new FileInputStream(fileName), "UTF-8"),
					confClass);
			if(conf==null) {
				System.out.println("file \""+fileName+"\" was Empty, creating file...");
				store(getDefault());
				return getDefault();
			}
			map.put(fileName, conf);
			if(firstReadLog && !runtimeUpdate) {
				System.out.println("the file \""+fileName+"\" imported successfully");
			}
			return conf;
		} catch (Exception e) {
			System.err.println("error while getting conf \""+fileName+"\", returned default\ncause by:");
			e.printStackTrace();
			return getDefault();
		}
		
	}
	
	private T getDefault() {
		T def = confClass.cast(dummyObject.getDefault());
		map.put(fileName, def);
		return def;
	}
	
	public void store(T configuration) {
		File c = new File(fileName);
		if (!c.exists()) {
			try {
				c.createNewFile();
			} catch (IOException e) {
				System.out.println("failed to create the file \""+fileName+"\"\ncause by:\"");
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
			System.out.println("failed to write to the file \""+fileName+"\"\ncause by:\"");
			e.printStackTrace();
			return;
		}
		System.out.println("the file \""+fileName+"\" created succesfully");
		System.out.println("Content of file:");
		System.out.println(gson.toJson(configuration, confClass));
	}
	
}
