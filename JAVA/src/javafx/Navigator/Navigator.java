package javafx.Navigator;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/** (Singleton) class for navigation between windows */
public class Navigator implements NavigatorInterface {

	private static NavigatorInterface instance = null;
	private static Pane baseNode = null;
	private static String defaultTab = null;
	@SuppressWarnings("rawtypes")
	private static Class fxmlAnchor = Navigator.class;
	
	private Tab current = null;

	private Stack<Tab> history;

	private Navigator() {
		if (baseNode == null)
			throw new RuntimeException("Navigator not initiated, run Navigator.init(Pane baseNode) first");
		history = new Stack<>();
	}
	
	public static void setNavigator(NavigatorInterface nav) {
		instance = nav;
	}

	/**
	 * <pre>
	 * (Singleton) get an instance of navigator
	 * Navigator.init() need to be called before the call to this function
	 * </pre>
	 * 
	 * @exception RuntimeException if Navigator.init() not called once before the
	 *                             call to this function
	 * @return Navigator - the instance of this class
	 */
	public static NavigatorInterface instance() {
		if (instance == null)
			instance = new Navigator();
		return instance;
	}

	/**
	 * initialize the Navigator to change the content of the given Pane
	 * 
	 * @param baseNode the node that the navigator need to change
	 */
	public static void init(Pane baseNode) {
		Navigator.baseNode = baseNode;
	}

	/**
	 * navigate to the given file(window) and push the current view to the history
	 * @return the controller of the screen
	 */
	@Override
	public Object navigate(String destenation) {
		String fxmlName = null;
		if (destenation == null) {
			baseNode.getChildren().clear();
			return null;
		}
		if (destenation.endsWith(".fxml"))
			fxmlName = destenation;
		else
			fxmlName = destenation + ".fxml";

		// push the current tab to the history
		if (current != null)
			history.push(current);
		
		URL screen = fxmlAnchor.getResource(fxmlName);
		if (screen == null)
			return navigate(null);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(screen);
		try {
			current = new Tab();
			current.node = loader.load();
			current.controller = loader.getController();
			current.name = destenation;
			baseNode.getChildren().clear();
			baseNode.getChildren().add(current.node);
			return current.controller;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NavigationInterruption e) {
			back();
		}
		return null;
	}

	/** navigates to the last page all the data from current page will be deleted 
	 * @return the controller of the screen
	 */
	@Override
	public Object back() {
		if (history.isEmpty())
			return null;
		Tab last = history.pop();
		current = last;
		baseNode.getChildren().clear();
		baseNode.getChildren().add(current.node);
		return current.controller;
	}

	/** navigates to the default page(empty Page) and clear the history 
	 * @return the controller of the screen
	 */
	@Override
	public Object clearHistory() {
		history.clear();
		current = null;
		navigate(defaultTab);
		if(current!=null)
			return current.controller;
		return null;
	}

	/**
	 * navigates to the given page and clear the history
	 * 
	 * @param fxml the page to navigate
	 * @return the controller of the screen
	 */
	@Override
	public Object clearHistory(String fxml) {
		history.clear();
		current = null;
		navigate(fxml);
		if(current!=null)
			return current.controller;
		return null;
	}

	/**
	 * gets the default tab to navigate after {@link #clearHistory()}
	 * @return the defaultTab
	 */
	public static String getDefaultTab() {
		return defaultTab;
	}

	/**
	 * sets the default tab to navigate after {@link #clearHistory()}
	 * @param defaultTab the defaultTab to set
	 */
	public static void setDefaultTab(String defaultTab) {
		Navigator.defaultTab = defaultTab;
	}
	
	
	/**
	 * sets anchor for the FXML files
	 * @param fxmlAnchor the fxmlAnchor to set, class in the folder of the FXML files
	 */
	@SuppressWarnings("rawtypes")
	public static void setFxmlAnchor(Class fxmlAnchor) {
		Navigator.fxmlAnchor = fxmlAnchor;
	}


	/** helper class for saving windows */
	private class Tab {
		public Node node;
		public Object controller;
		public String name;

		public Tab(Node body, Object controller, String name) {
			super();
			this.node = body;
			this.controller = controller;
			this.name = name;
		}

		public Tab() {
			super();
		}

	}

	/** navigation Interruption */
	public static class NavigationInterruption extends RuntimeException {

		private static final long serialVersionUID = 3626458317670172388L;

	}
}
