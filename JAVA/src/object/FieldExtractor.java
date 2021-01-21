package object;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * class for extracting fields or sub-classes<br>
 * see {@link #extractField(Collection, String, Class, Class)},<br>{@link #subClass(Collection, Class, Class)}
 * @author Or Man
 * @version 1.2
 * @since 21/01/2021
 */
public class FieldExtractor {

	/**
	 * extract field from the given List(of some Object) to list of specific TYPE<br>
	 * for example in class of Student{int id, String name} use this to extract list of the student Id's(List&lt;int&gt;)
	 * @param <T> Type of the main class(Student in our example)
	 * @param <E> Type of the Field to extract(int in our example)
	 * @param collection - {@link Collection} &lt;T&gt; from type T, will extract the field from this collection
	 * @param fieldName - {@link String} the name of the field to extract
	 * @param mainClass - {@link Class} of the Collection
	 * @param returnClass - {@link Class} of the field to extract
	 * @return {@link Collection} &lt;E&gt; of the values of "field" in each object from the mainCollection
	 * @throws NoSuchFieldException - if the class <b>T</b> not contains field in the name <b>fieldName</b>
	 * @throws SecurityException - If a security manager, s, is present and the caller's class loader is not the same as or anancestor of the class loader for the current class and invocation of s.checkPackageAccess() denies access to the package of this class.<br>see {@link Class#getField(String)}
	 * @throws IllegalArgumentException - if the Field <b>fieldName</b> in the class <b>mainClass</b> is not of type <b>E</b>
	 * @throws IllegalAccessException - if the Field <b>fieldName</b> in the class <b>mainClass</b> is private, see {@link Field#get(Object)} 
	 * @see {@link Class#getField(String)} ,{@link Field#get(Object)} ,{@link Class#cast(Object)}
	 */
	public static <T,E> Collection<E> extractField(Collection<? extends T> collection, String fieldName,Class<T> mainClass,Class<E> returnClass) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Field f = mainClass.getField(fieldName);
		if(!f.getType().equals(returnClass))
			throw new IllegalArgumentException("Field "+fieldName+" is of type "+f.getType()+" and not type "+returnClass);
		Collection<E> ret = new ArrayList<E>(collection.size());
		for(T obj : collection) {
			ret.add(returnClass.cast(f.get(obj)));
		}
		return ret;
	}
	
	/**
	 * extract sub-class from the given List(of some Object) to list of specific TYPE<br>
	 * for example in class of Student{int id, String name, Double grade} use this to extract list of the students name's and Id's(Class NameAndID{int id, String name} ) - List&lt;NameAndID&gt;
	 * @param <T> Type of the main class(Student in our example)
	 * @param <E> Type of the sub class(NameAndID in our example)
	 * @param collection - {@link Collection} &lt;T&gt; from type T, will extract the class from this collection
	 * @param mainClass - {@link Class} of the Collection
	 * @param returnClass - {@link Class} of the sub Class to extract
	 * @return {@link Collection} &lt;E&gt; of the values of the fields of returnClass in each object from the nainCollection
	 * @throws NoSuchFieldException - if the class <b>T</b> not contains field in the name <b>fieldName</b>, where <b>fieldName</b> is name of each field of <b>returnClass</b>
	 * @throws SecurityException - If a security manager, s, is present and the caller's class loader is not the same as or anancestor of the class loader for the current class and invocation of s.checkPackageAccess() denies access to the package of this class.<br>see {@link Class#getField(String)}
	 * @throws IllegalAccessException - if the Field <b>fieldName</b> in the class <b>mainClass</b> or <b>returnClass</b> is private, see {@link Field#get(Object)} 
	 * @throws InstantiationException - if the <b>returnClass</b> not contains empty constructor
	 * @see {@link Class#getField(String)} ,{@link Field#get(Object)} ,{@link Class#cast(Object)}
	 */
	public static <T,E> Collection<E> subClass(Collection<? extends T> collection,Class<T> mainClass,Class<E> returnClass) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{
		Field[] retFields = returnClass.getFields();
		Field[] mainFields = new Field[retFields.length];
		for(int i=0 ; i< retFields.length;i++) {
			Field f = retFields[i];
			mainFields[i] = mainClass.getField(f.getName());
		}
		Collection<E> ret = new ArrayList<E>(collection.size());
		for(T obj : collection) {
			E newObj = returnClass.newInstance();
			for(int i=0 ; i< retFields.length;i++) {
				Field f = retFields[i];
				f.set(newObj, mainFields[i].get(obj));
			}
			ret.add(newObj);
		}
		return ret;
	}
	

}
