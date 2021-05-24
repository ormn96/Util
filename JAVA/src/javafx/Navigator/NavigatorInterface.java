package javafx.Navigator;

public interface NavigatorInterface {

	/**
	 * navigate to the given file(window) and push the current view to the history
	 */
	Object navigate(String destenation);

	/** navigates to the last page all the data from current page will be deleted 
	 * @return */
	Object back();

	/** navigates to the default page(empty Page) and clear the history 
	 * @return */
	Object clearHistory();

	/**
	 * navigates to the given page and clear the history
	 * 
	 * @param fxml the page to navigate
	 * @return 
	 */
	Object clearHistory(String fxml);

}