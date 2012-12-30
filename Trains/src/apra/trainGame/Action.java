package apra.trainGame;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Models a particular action as a subject, method, and set of parameters.
 * 
 * @author Rob Argue
 */
public class Action {
	private Method method;
	private Object subject;
	private Object[] args;
	
	/**
	 * Constructor for creating an <code>Action</code>
	 * 
	 * @param subject <code>Object</code> to perform the action on	
	 * @param subject <code>Method</code> to perform on the subject
	 * @param args List of arguments for the <code>Method</code>
	 */
	public Action(Object subject, Method method, Object[] args)
	{
		this.method = method;
		this.subject = subject;
		this.args = args;
	}
	
	/**
	 * Executes the action by invoking the method on the subject.
	 */
	public void Execute()
	{
		try {
			method.invoke(subject, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
